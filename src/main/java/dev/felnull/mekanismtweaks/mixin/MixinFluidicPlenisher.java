package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Config;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.api.Action;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.TileEntityFluidicPlenisher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TileEntityFluidicPlenisher.class, remap = false)
public abstract class MixinFluidicPlenisher {

    @Shadow
    private MachineEnergyContainer<TileEntityFluidicPlenisher> energyContainer;

    @Shadow
    private void doPlenish() {
    }

    /**
     * Mekanism's Fluidic Plenisher plenishes once per tick at the most, however many Speed Upgrades it has.
     * Let it plenish more in the same tick, like the other machines do, when the time per operation is below one tick.
     * Every extra operation costs the energy of one tick.
     */
    @Redirect(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityFluidicPlenisher;doPlenish()V"))
    private void mekanismtweaks$plenishMore(TileEntityFluidicPlenisher plenisher) {
        doPlenish();
        int operations = Math.min(UpgradeEffect.operationsPerTick(plenisher, TileEntityFluidicPlenisher.BASE_TICKS_REQUIRED), Config.maxPlenisherOperations());
        FloatingLong energyPerTick = energyContainer.getEnergyPerTick();
        for (int i = 1; i < operations; i++) {
            // nothing more to do once the calculation has finished or the tank has run dry
            if (plenisher.finishedCalc || plenisher.fluidTank.isEmpty()) {
                break;
            }
            if (!energyContainer.extract(energyPerTick, Action.SIMULATE, AutomationType.INTERNAL).equals(energyPerTick)) {
                break;
            }
            energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
            doPlenish();
        }
    }
}
