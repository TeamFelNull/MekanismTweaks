package dev.felnull.mekanismtweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mekanism.common.Mekanism;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.network.PacketRemoveUpgrade;
import mekanism.common.network.PacketRemoveUpgrade.RemoveUpgradeMessage;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.tile.component.TileComponentUpgrade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PacketRemoveUpgrade.class, remap = false)
public abstract class MixinPacketRemoveUpgrade {

    @ModifyExpressionValue(method = "lambda$onMessage$0", at = @At(value = "FIELD", target = "Lmekanism/common/network/PacketRemoveUpgrade$RemoveUpgradeMessage;upgradeType:I", opcode = Opcodes.GETFIELD))
    private static int decodeUpgradeType(int type) {
        return type % Upgrade.values().length;
    }

    @WrapOperation(method = "lambda$onMessage$0", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/component/TileComponentUpgrade;removeUpgrade(Lmekanism/common/Upgrade;)V"))
    private static void removeAllUpgrades(TileComponentUpgrade component, Upgrade upgrade, Operation<Void> original, RemoveUpgradeMessage message, EntityPlayer player) {
        do original.call(component, upgrade);
        while (message.upgradeType >= Upgrade.values().length && component.getUpgrades(upgrade) > 0 && player.inventory.addItemStackToInventory(upgrade.getStack()));
    }
}
