package dev.felnull.mekanismtweaks.mixin.operate;

import dev.felnull.mekanismtweaks.IOperationData;
import mekanism.common.tile.prefab.TileEntityOperationalMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityOperationalMachine.class, remap = false)
public class OperationalMachine implements IOperationData {
    @Shadow public int ticksRequired;

    @Shadow public int operatingTicks;

    @Override
    public int reqTime() {
        return ticksRequired;
    }

    @Override
    public int opeTime() {
        return operatingTicks;
    }

    @Override
    public void setOpeTime(int operatingTicks) {
        this.operatingTicks = operatingTicks;
    }

    @Inject(method = "getScaledProgress", at = @At("HEAD"), cancellable = true)
    public void getScaledProgress(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(getScaledOpeTime());
        cir.cancel();
    }
}
