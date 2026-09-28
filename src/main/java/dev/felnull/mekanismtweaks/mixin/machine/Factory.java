package dev.felnull.mekanismtweaks.mixin.machine;

import mekanism.common.tile.TileEntityFactory;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityFactory.class, remap = false)
public abstract class Factory {

    @Unique
    private int[] mekanismTweaks$resetSink;

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityFactory;operate(II)V"))
    private void accumulateExcessProgress(TileEntityFactory instance, int inputSlot, int outputSlot) {
        instance.operate(inputSlot, outputSlot);
        if (ticksRequired < 0) {
            int process = inputSlot - getInputSlot(0);
            if (progress[process] < 0 || progress[process] >= 20)
                progress[process] = Math.max(0, progress[process] % 20);
            progress[process] -= ticksRequired;
        }
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityFactory;progress:[I", opcode = Opcodes.GETFIELD, ordinal = 3))
    private int[] modifyOperatingTicksLater(TileEntityFactory instance) {
        if (ticksRequired >= 0)
            return instance.progress;
        if (mekanismTweaks$resetSink == null || mekanismTweaks$resetSink.length != progress.length)
            mekanismTweaks$resetSink = new int[progress.length];
        return mekanismTweaks$resetSink;
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void handleExcessOperations(CallbackInfo ci) {
        if (ticksRequired >= 0)
            return;

        int secondaryEnergy = secondaryEnergyThisTick;
        secondaryEnergyThisTick = 0;
        try {
            boolean operated;
            do {
                operated = false;
                for (int process = 0; process < progress.length; process++) {
                    if (progress[process] < 20)
                        continue;

                    int inputSlot = getInputSlot(process);
                    int outputSlot = getOutputSlot(process);
                    if (canOperate(inputSlot, outputSlot)) {
                        progress[process] -= 20;
                        operate(inputSlot, outputSlot);
                        operated = true;
                    } else {
                        progress[process] %= 20;
                    }
                }
            } while (operated);
        } finally {
            secondaryEnergyThisTick = secondaryEnergy;
        }
    }

    @Shadow
    public int ticksRequired;

    @Shadow
    private int secondaryEnergyThisTick;

    @Shadow
    public int[] progress;

    @Shadow
    public abstract void operate(int inputSlot, int outputSlot);

    @Shadow
    public abstract boolean canOperate(int inputSlot, int outputSlot);

    @Shadow
    public abstract int getInputSlot(int operation);

    @Shadow
    public abstract int getOutputSlot(int operation);

    @Inject(method = "getScaledProgress(II)I", at = @At("HEAD"), cancellable = true)
    private void getScaledProgress(int i, int process, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Math.min(20, ticksRequired > 0 ? i * progress[process] / ticksRequired : progress[process]));
    }
}
