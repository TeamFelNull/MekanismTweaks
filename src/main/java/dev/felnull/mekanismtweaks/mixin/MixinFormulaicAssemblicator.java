package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Config;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.machine.TileEntityFormulaicAssemblicator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TileEntityFormulaicAssemblicator.class, remap = false)
public abstract class MixinFormulaicAssemblicator {

    @Shadow
    @Final
    private static int BASE_TICKS_REQUIRED;

    @Shadow
    private boolean doSingleCraft() {
        return false;
    }

    /**
     * Mekanism's Formulaic Assemblicator crafts once per tick at the most, however many Speed Upgrades it has.
     * Let it craft more in the same tick, like the other machines do, when the time per craft is below one tick.
     * Every extra craft costs the energy of one tick.
     */
    @Redirect(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/machine/TileEntityFormulaicAssemblicator;doSingleCraft()Z"))
    private boolean mekanismtweaks$craftMore(TileEntityFormulaicAssemblicator assemblicator) {
        boolean crafted = doSingleCraft();
        if (!crafted) {
            return false;
        }
        int operations = Math.min(UpgradeEffect.operationsPerTick(assemblicator, BASE_TICKS_REQUIRED), Config.maxAssemblicatorOperations());
        FloatingLong energyPerTick = assemblicator.getEnergyContainer().getEnergyPerTick();
        for (int i = 1; i < operations; i++) {
            if (!assemblicator.getEnergyContainer().extract(energyPerTick, Action.SIMULATE, AutomationType.INTERNAL).equals(energyPerTick)) {
                break;
            }
            assemblicator.getEnergyContainer().extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
            if (!doSingleCraft()) {
                break;
            }
        }
        return true;
    }
}
