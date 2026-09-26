package dev.felnull.mekanismtweaks.mixin.machine;

import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityBasicMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityBasicMachine.class, remap = false)
public abstract class MixinBasicMachine {

    /**
     * Display full progress instead of a meaningless ratio while performing at least one operation per tick.
     */
    @Inject(method = "getScaledProgress", at = @At("HEAD"), cancellable = true)
    private void scaledProgress(CallbackInfoReturnable<Double> cir) {
        TileEntityBasicMachine self = (TileEntityBasicMachine) (Object) this;
        Double progress = Temp.scaledProgress(self, self.TICKS_REQUIRED, self.isActive);
        if (progress != null) cir.setReturnValue(progress);
    }
}
