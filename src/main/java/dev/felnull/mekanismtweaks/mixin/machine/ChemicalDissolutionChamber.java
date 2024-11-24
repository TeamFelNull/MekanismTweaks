package dev.felnull.mekanismtweaks.mixin.machine;

import dev.felnull.mekanismtweaks.IOperationData;
import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityChemicalDissolutionChamber;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityChemicalDissolutionChamber.class, remap = false)
public abstract class ChemicalDissolutionChamber implements IOperationData {

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityChemicalDissolutionChamber;operate(Lmekanism/common/recipe/machines/DissolutionRecipe;)V"))
    private void confirmOperated(CallbackInfo ci) {
        Temp.hasOperated.set(true);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityChemicalDissolutionChamber;operatingTicks:I", opcode = Opcodes.PUTFIELD, ordinal = 1))
    private void modifyOperatingTicksLater(TileEntityChemicalDissolutionChamber instance, int value) {
        Temp.modifyOperatingTicksLater((IOperationData) (Object)instance, value);
    }

    @Inject(method = "onUpdate", at = @At("TAIL") /*@At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityChemicalDissolutionChamber;canOperate(Lmekanism/common/recipe/machines/DissolutionRecipe;)Z", ordinal = 1, shift = At.Shift.BY, by = -4)*/)
    public void handleExcessOperations(CallbackInfo ci) {
        Temp.inject2(this, this::onUpdate);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityChemicalDissolutionChamber;energyPerTick:D", opcode = Opcodes.GETFIELD))
    private double correctEnergyPerTick(TileEntityChemicalDissolutionChamber instance) {
        return Temp.correctEnergyPerTick(instance);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityChemicalDissolutionChamber;injectUsage:D", opcode = Opcodes.GETFIELD))
    private double correctGasPerTick(TileEntityChemicalDissolutionChamber instance) {
        return Temp.isInjecting.get() ? 0 : injectUsage;
    }

    @Shadow
    public int ticksRequired;

    @Shadow
    public int operatingTicks;

    @Shadow public double injectUsage;

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
