package dev.felnull.mekanismtweaks.mixin.machine;

import dev.felnull.mekanismtweaks.IExcess;
import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityChemicalCrystallizer;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityChemicalCrystallizer.class, remap = false)
public abstract class MixinChemicalCrystallizer implements IExcess {

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "operate", at = @At("HEAD"))
    private void confirmOperated(CallbackInfo ci) {
        Temp.hasOperated.set(true);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void handleExcessOperations(CallbackInfo ci) {
        TileEntityChemicalCrystallizer self = (TileEntityChemicalCrystallizer) (Object) this;
        Temp.afterUpdate(self, MekanismUtils.getTicks(self, self.TICKS_REQUIRED), this::onUpdate);
    }

    @Inject(method = "getScaledProgress", at = @At("HEAD"), cancellable = true)
    private void scaledProgress(CallbackInfoReturnable<Double> cir) {
        TileEntityChemicalCrystallizer self = (TileEntityChemicalCrystallizer) (Object) this;
        Double progress = Temp.scaledProgress(self, self.TICKS_REQUIRED, self.isActive);
        if (progress != null) cir.setReturnValue(progress);
    }
}
