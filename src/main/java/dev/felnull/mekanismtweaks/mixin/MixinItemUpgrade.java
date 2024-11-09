package dev.felnull.mekanismtweaks.mixin;

import mekanism.common.item.ItemUpgrade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ItemUpgrade.class, remap = false)
public class MixinItemUpgrade {

    /**
     * Limit MaxStackSize to 64 to avoid unstable item interaction behavior.
     */
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/item/ItemUpgrade;func_77625_d(I)Lnet/minecraft/item/Item;"))
    private int injected(int par1) {
        return Math.min(par1, 64);
    }
}
