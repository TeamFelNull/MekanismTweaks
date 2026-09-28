package dev.felnull.mekanismtweaks.mixin;

import mekanism.common.Mekanism;
import mekanism.common.PacketHandler;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.network.PacketRemoveUpgrade;
import mekanism.common.network.PacketRemoveUpgrade.RemoveUpgradeMessage;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PacketRemoveUpgrade.class, remap = false)
public abstract class MixinPacketRemoveUpgrade {

    @Inject(method = "onMessage(Lmekanism/common/network/PacketRemoveUpgrade$RemoveUpgradeMessage;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;", at = @At("HEAD"), cancellable = true)
    private void removeAllUpgrades(RemoveUpgradeMessage message, MessageContext context, CallbackInfoReturnable<IMessage> cir) {
        Upgrade[] upgrades = Upgrade.values();
        if (message.upgradeType < upgrades.length)
            return;

        int upgradeType = message.upgradeType - upgrades.length;
        if (upgradeType >= upgrades.length) {
            cir.setReturnValue(null);
            return;
        }

        EntityPlayer player = PacketHandler.getPlayer(context);
        PacketHandler.handlePacket(() -> {
            TileEntity tileEntity = message.coord4D.getTileEntity(player.world);
            if (!(tileEntity instanceof IUpgradeTile) || !(tileEntity instanceof TileEntityBasicBlock))
                return;

            IUpgradeTile upgradeTile = (IUpgradeTile) tileEntity;
            Upgrade upgrade = upgrades[upgradeType];
            boolean removed = false;
            while (upgradeTile.getComponent().getUpgrades(upgrade) > 0) {
                ItemStack stack = upgrade.getStack();
                if (!player.inventory.addItemStackToInventory(stack))
                    break;
                upgradeTile.getComponent().removeUpgrade(upgrade);
                removed = true;
            }

            if (removed) {
                player.inventory.markDirty();
                tileEntity.markDirty();
                Mekanism.packetHandler.sendUpdatePacket((TileEntityBasicBlock) tileEntity);
            }
        }, player);
        cir.setReturnValue(null);
    }
}
