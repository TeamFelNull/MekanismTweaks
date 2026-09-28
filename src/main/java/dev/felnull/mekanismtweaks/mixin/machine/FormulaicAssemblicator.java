package dev.felnull.mekanismtweaks.mixin.machine;

import dev.felnull.mekanismtweaks.IOperationData;
import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityFormulaicAssemblicator;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityFormulaicAssemblicator.class, remap = false)
public abstract class FormulaicAssemblicator implements IOperationData {

    @Shadow
    public abstract void onUpdate();

    @Invoker("doSingleCraft")
    protected abstract boolean invokeDoSingleCraft();

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityFormulaicAssemblicator;doSingleCraft()Z"))
    private boolean craftIfPowered(TileEntityFormulaicAssemblicator instance) {
        if (!Temp.isInjecting.get() && instance.getEnergy() < instance.energyPerTick)
            return false;
        boolean operated = invokeDoSingleCraft();
        Temp.hasOperated.set(operated);
        return operated;
    }

    @Inject(method = "onUpdate", at = @At(value = "TAIL"))
    public void handleExcessOperations(CallbackInfo ci) {
        Temp.injectProgress(this, this::onUpdate);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityFormulaicAssemblicator;operatingTicks:I", opcode = Opcodes.PUTFIELD, ordinal = 0))
    private void modifyOperatingTicksLaterANDConsumeEnergy(TileEntityFormulaicAssemblicator instance, int value) {
        Temp.modifyOperatingTicksLater(this, value);
        instance.setEnergy(instance.getEnergy() - correctEnergyPerTick(instance));
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityFormulaicAssemblicator;energyPerTick:D", opcode = Opcodes.GETFIELD))
    private double correctEnergyPerTick(TileEntityFormulaicAssemblicator instance) {
        return Temp.isInjecting.get() ? 0 : instance.energyPerTick;
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityFormulaicAssemblicator;operatingTicks:I", opcode = Opcodes.GETFIELD, ordinal = 0))
    private int correctSpeed(TileEntityFormulaicAssemblicator instance) {
        return instance.operatingTicks + 1;
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
