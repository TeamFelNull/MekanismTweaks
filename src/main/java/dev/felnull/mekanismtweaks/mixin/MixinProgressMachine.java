package dev.felnull.mekanismtweaks.mixin;

import mekanism.common.tile.prefab.TileEntityProgressMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityProgressMachine.class)
public abstract class MixinProgressMachine {

    @Shadow
    public int ticksRequired;

    /**
     * Display full progress instead of a meaningless ratio while the machine does at least one operation per tick.
     */
    @Inject(method = "getScaledProgress", at = @At("HEAD"), cancellable = true)
    private void mekanismtweaks$scaledProgress(CallbackInfoReturnable<Double> cir) {
        if (ticksRequired <= 1) {
            cir.setReturnValue(((TileEntityProgressMachine<?>) (Object) this).getActive() ? 1D : 0D);
        }
    }
}
