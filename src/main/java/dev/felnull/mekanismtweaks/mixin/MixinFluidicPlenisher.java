package dev.felnull.mekanismtweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.felnull.mekanismtweaks.Config;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.common.tile.machine.TileEntityFluidicPlenisher;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TileEntityFluidicPlenisher.class)
public abstract class MixinFluidicPlenisher {

    /**
     * Mekanism's Fluidic Plenisher plenishes once per tick at the most, however many Speed Upgrades it has.
     * Let it plenish more in the same tick, like the other machines do, when the time per operation is below one tick.
     * Every extra operation costs the energy of one tick.
     */
    @WrapOperation(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/machine/TileEntityFluidicPlenisher;doPlenish()V"))
    private void mekanismtweaks$plenishMore(TileEntityFluidicPlenisher plenisher, Operation<Void> original) {
        original.call(plenisher);
        int operations = Math.min(MekanismUtils.getOperationsPerTick(plenisher, TileEntityFluidicPlenisher.BASE_TICKS_REQUIRED, 1), Config.maxPlenisherOperations());
        long energyPerTick = plenisher.getEnergyContainer().getEnergyPerTick();
        for (int i = 1; i < operations; i++) {
            // nothing more to do once the calculation has finished or the tank has run dry
            if (plenisher.finishedCalc || plenisher.fluidTank.isEmpty()) {
                break;
            }
            if (plenisher.getEnergyContainer().extract(energyPerTick, Action.SIMULATE, AutomationType.INTERNAL) != energyPerTick) {
                break;
            }
            plenisher.getEnergyContainer().extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
            original.call(plenisher);
        }
    }
}
