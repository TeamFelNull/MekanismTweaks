package dev.felnull.mekanismtweaks.mixin.machine;


import dev.felnull.mekanismtweaks.IOperationData;
import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityFluidicPlenisher;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityFluidicPlenisher.class, remap = false)
public abstract class FluidicPlenisher implements IOperationData {

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityFluidicPlenisher;doPlenish()V"))
    private void confirmOperated(CallbackInfo ci) {
        Temp.hasOperated.set(true);
    }

    @Inject(method = "onUpdate", at = @At(value = "TAIL"))
    public void handleExcessOperations(CallbackInfo ci) {
        Temp.inject2(this, this::onUpdate);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityFluidicPlenisher;operatingTicks:I", opcode = Opcodes.PUTFIELD, ordinal = 1))
    private void modifyOperatingTicksLater(TileEntityFluidicPlenisher instance, int value) {
        Temp.modifyOperatingTicksLater((IOperationData) (Object) instance, value);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityFluidicPlenisher;energyPerTick:D", opcode = Opcodes.GETFIELD))
    private double correctEnergyPerTick(TileEntityFluidicPlenisher instance) {
        return Temp.isInjecting.get() ? 0 : instance.energyPerTick;
    }

    @Shadow
    public int ticksRequired;

    @Shadow
    public int operatingTicks;

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
}
