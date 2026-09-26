package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.MekanismTweaks;
import mekanism.common.item.ItemMachineUpgrade;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemMachineUpgrade.class, remap = false)
public class MixinItemMachineUpgrade {

    /**
     * Limit MaxStackSize to 64 to avoid unstable item interaction behavior.
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void stackSize(CallbackInfo ci) {
        ((Item) (Object) this).setMaxStackSize(Math.min(64, Math.max(MekanismTweaks.maxSpeed, MekanismTweaks.maxEnergy)));
    }
}
