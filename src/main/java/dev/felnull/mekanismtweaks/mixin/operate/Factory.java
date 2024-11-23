package dev.felnull.mekanismtweaks.mixin.operate;


import dev.felnull.mekanismtweaks.IOperationData;
import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.base.IFactory;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.Arrays;

@Mixin(value = TileEntityFactory.class, remap = false)
public abstract class Factory implements IOperationData {

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityFactory;operate(II)V"))
    private void confirmOperated(CallbackInfo ci) {
        Temp.hasOperated.set(true);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityFactory;progress:[I", ordinal = 3))
    private int[] modifyOperatingTicksLater(TileEntityFactory instance) {
        return ticksRequired > 0 ? instance.progress : new int[progress.length];
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    public void handleExcessOperations(CallbackInfo ci) {
        Temp.inject2(this, this::onUpdate);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityFactory;energyPerTick:D", opcode = Opcodes.GETFIELD))
    private double correctEnergyPerTick(TileEntityFactory instance) {
        return Temp.correctEnergyPerTick(instance);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityFactory;secondaryEnergyPerTick:D", opcode = Opcodes.GETFIELD))
    private double correctGasPerTick(TileEntityFactory instance) {
        return Temp.isInjecting.get() ? 0 : this.secondaryEnergyPerTick;
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityFactory;lastUsage:D", opcode = Opcodes.PUTFIELD))
    private void correctUsage(TileEntityFactory instance, double value) {
        if (!Temp.isInjecting.get()) instance.lastUsage = value;
    }


    @Shadow
    public int ticksRequired;

    @Shadow
    private double secondaryEnergyPerTick;

    @Shadow
    public int[] progress;

    @Shadow
    public abstract boolean canOperate(int inputSlot, int outputSlot);

    @Shadow
    public abstract int getInputSlot(int operation);

    @Shadow
    public abstract int getOutputSlot(int operation);

    @Shadow
    @Nonnull
    private IFactory.RecipeType recipeType;

    @Override
    public int reqTime() {
        return ticksRequired;
    }

    @Override
    public int opeTime() {
        return Arrays.stream(progress).max().getAsInt();
    }

    @Override
    public void setOpeTime(int operatingTicks) {
        Arrays.fill(progress, operatingTicks);
    }

    @Inject(method = "getScaledProgress", at = @At("HEAD"), cancellable = true)
    public void getScaledProgress(int i, int process, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Math.min(20, reqTime() > 0 ? i * progress[process] / reqTime() : progress[process]));
        cir.cancel();
    }

    @Override
    public void addOpeTime(int opeTime) {
        for (int process = 0; process < progress.length; process++)
            //for operating slots, copy-pasted from original code
            if (!(!canOperate(getInputSlot(process), getOutputSlot(process)) && (recipeType.getFuelType() != IFactory.MachineFuelType.ADVANCED || !recipeType.hasRecipe(((TileEntityContainerBlock) (Object) this).inventory.get(this.getInputSlot(process))))))
                progress[process] += opeTime;
    }

    @Override
    public void subOpeTime(int opeTime) {
        for (int process = 0; process < progress.length; process++)
            if (progress[process] >= opeTime)
                progress[process] -= opeTime;
            else
                progress[process] = -progress[process] + reqTime(); // below reqTime to avoid operation. Although it would be subject to progress[process]++.
    }

    @Override
    public void postProcessing() {
        for (int process = 0; process < progress.length; process++)
            if (progress[process] < 0)
                progress[process] = -progress[process] % -reqTime() - progress[process] / reqTime(); // restore the original excess progress and remove incremental components
    }
}