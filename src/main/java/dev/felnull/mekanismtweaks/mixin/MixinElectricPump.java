package dev.felnull.mekanismtweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.felnull.mekanismtweaks.Config;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.common.tile.machine.TileEntityElectricPump;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TileEntityElectricPump.class)
public abstract class MixinElectricPump {

    @Shadow
    @Final
    private static int BASE_TICKS_REQUIRED;

    /**
     * Mekanism's Electric Pump pumps once per tick at the most, however many Speed Upgrades it has.
     * Let it pump more in the same tick, like the other machines do, when the time per operation is below one tick.
     * Every extra operation costs the energy of one tick.
     */
    @WrapOperation(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/machine/TileEntityElectricPump;suck()Z"))
    private boolean mekanismtweaks$suckMore(TileEntityElectricPump pump, Operation<Boolean> original) {
        boolean sucked = original.call(pump);
        if (!sucked) {
            return false;
        }
        int operations = Math.min(MekanismUtils.getOperationsPerTick(pump, BASE_TICKS_REQUIRED, 1), Config.maxPumpOperations());
        long energyPerTick = pump.getEnergyContainer().getEnergyPerTick();
        for (int i = 1; i < operations; i++) {
            // the tank must have room for another operation, and there must be energy for it
            if (!pump.fluidTank.isEmpty() && pump.estimateIncrementAmount() > pump.fluidTank.getNeeded()) {
                break;
            }
            if (pump.getEnergyContainer().extract(energyPerTick, Action.SIMULATE, AutomationType.INTERNAL) != energyPerTick) {
                break;
            }
            pump.getEnergyContainer().extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
            if (!original.call(pump)) {
                break;
            }
        }
        return true;
    }
}
