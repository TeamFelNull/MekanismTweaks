package dev.felnull.mekanismtweaks.mixin;

import mekanism.common.Mekanism;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeItem;
import mekanism.common.tile.component.TileComponentUpgrade;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileComponentUpgrade.class, remap = false)
public class MixinTileComponentUpgrade {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void installAllUpgrades(CallbackInfo ci) {
        TileComponentUpgrade component = (TileComponentUpgrade) (Object) this;
        if (!component.tileEntity.getWorld().isRemote) {
            ItemStack stack = component.tileEntity.inventory.get(component.getUpgradeSlot());
            if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeItem) {
                Upgrade type = ((IUpgradeItem) stack.getItem()).getUpgradeType(stack);
                if (component.supports(type) && component.getUpgrades(type) < type.getMax()) {
                    if (component.upgradeTicks < TileComponentUpgrade.UPGRADE_TICKS_REQUIRED) {
                        component.upgradeTicks++;
                    } else if (component.upgradeTicks == TileComponentUpgrade.UPGRADE_TICKS_REQUIRED) {
                        component.upgradeTicks = 0;
                        int amount = Math.min(stack.getCount(), type.getMax() - component.getUpgrades(type));
                        for (int i = 0; i < amount; i++)
                            component.addUpgrade(type);
                        stack.shrink(amount);
                        Mekanism.packetHandler.sendUpdatePacket(component.tileEntity);
                        component.tileEntity.markDirty();
                    }
                } else {
                    component.upgradeTicks = 0;
                }
            } else {
                component.upgradeTicks = 0;
            }
        }
        ci.cancel();
    }
}
