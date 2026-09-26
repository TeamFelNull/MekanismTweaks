package dev.felnull.mekanismtweaks.mixin;

import mekanism.common.tile.factory.TileEntityFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityFactory.class)
public abstract class MixinFactory {

    @Shadow
    private int ticksRequired;

    /**
     * Display full progress instead of a meaningless ratio while the factory does at least one operation per tick.
     */
    @Inject(method = "getScaledProgress", at = @At("HEAD"), cancellable = true)
    private void mekanismtweaks$scaledProgress(int i, int process, CallbackInfoReturnable<Double> cir) {
        if (ticksRequired <= 1) {
            cir.setReturnValue(((TileEntityFactory<?>) (Object) this).getActive() ? (double) i : 0D);
        }
    }
}
