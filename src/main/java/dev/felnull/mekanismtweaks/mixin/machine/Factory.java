package dev.felnull.mekanismtweaks.mixin.machine;

import mekanism.common.tile.TileEntityFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityFactory.class, remap = false)
public abstract class Factory {

    @Shadow
    public int ticksRequired;

    /**
     * Excess progress of each process. (20 progress per excess operation, as the other machines)
     */
    @Unique
    private final int[] excess = new int[64];

    /**
     * If ticksRequired is zero or negative, the factory operates every tick and additionally performs the excess operations.
     * Each of them consumes the energy and the gas of one tick, and is performed only while it can be.
     */
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityFactory;operate(II)V"))
    private void operateMore(TileEntityFactory factory, int in, int out) {
        factory.operate(in, out);
        if (ticksRequired > 0) {
            return;
        }
        int progress = excess[in & 63] - ticksRequired;
        for (int k = 2; progress >= 20; k++) {
            if (!factory.canOperate(in, out)
                    || factory.getEnergy() < factory.energyPerTick * k
                    || factory.gasTank.getStored() < factory.secondaryEnergyThisTick * k) {
                break;
            }
            factory.operate(in, out);
            factory.setEnergy(factory.getEnergy() - factory.energyPerTick);
            if (factory.secondaryEnergyThisTick > 0) {
                factory.gasTank.draw(factory.secondaryEnergyThisTick, true);
            }
            progress -= 20;
        }
        excess[in & 63] = Math.min(progress, 20);
    }

    /**
     * Display full progress instead of dividing by a non-positive number.
     */
    @Inject(method = "getScaledProgress", at = @At("HEAD"), cancellable = true)
    private void scaledProgress(int i, int process, CallbackInfoReturnable<Integer> cir) {
        if (ticksRequired <= 0) {
            cir.setReturnValue(((TileEntityFactory) (Object) this).getActive() ? i : 0);
        }
    }
}
