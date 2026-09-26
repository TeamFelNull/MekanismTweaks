package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Config;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.api.Action;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.TileEntityFormulaicAssemblicator;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TileEntityFormulaicAssemblicator.class, remap = false)
public abstract class MixinFormulaicAssemblicator {

    @Shadow
    private MachineEnergyContainer<TileEntityFormulaicAssemblicator> energyContainer;


    @Shadow
    private boolean doSingleCraft() {
        return false;
    }

    /**
     * Mekanism's Formulaic Assemblicator crafts once per tick at the most, however many Speed Upgrades it has.
     * Let it craft more in the same tick, like the other machines do, when the time per craft is below one tick.
     * Every extra craft costs the energy of one tick.
     */
    @Redirect(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityFormulaicAssemblicator;doSingleCraft()Z"))
    private boolean mekanismtweaks$craftMore(TileEntityFormulaicAssemblicator assemblicator) {
        boolean crafted = doSingleCraft();
        if (!crafted) {
            return false;
        }
        int operations = Math.min(UpgradeEffect.operationsPerTick(assemblicator, assemblicator.BASE_TICKS_REQUIRED), Config.maxAssemblicatorOperations());
        FloatingLong energyPerTick = energyContainer.getEnergyPerTick();
        for (int i = 1; i < operations; i++) {
            if (!energyContainer.extract(energyPerTick, Action.SIMULATE, AutomationType.INTERNAL).equals(energyPerTick)) {
                break;
            }
            energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
            if (!doSingleCraft()) {
                break;
            }
        }
        return true;
    }
}
