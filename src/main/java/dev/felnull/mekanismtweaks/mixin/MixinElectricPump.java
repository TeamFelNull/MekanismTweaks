package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Config;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.api.Action;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.TileEntityElectricPump;
import net.minecraftforge.fluids.FluidAttributes;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TileEntityElectricPump.class, remap = false)
public abstract class MixinElectricPump {


    @Shadow
    private MachineEnergyContainer<TileEntityElectricPump> energyContainer;

    @Shadow
    private boolean suck() {
        return false;
    }

    /**
     * Mekanism's Electric Pump pumps once per tick at the most, however many Speed Upgrades it has.
     * Let it pump more in the same tick, like the other machines do, when the time per operation is below one tick.
     * Every extra operation costs the energy of one tick.
     */
    @Redirect(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityElectricPump;suck()Z"))
    private boolean mekanismtweaks$suckMore(TileEntityElectricPump pump) {
        boolean sucked = suck();
        if (!sucked) {
            return false;
        }
        int operations = Math.min(UpgradeEffect.operationsPerTick(pump, pump.BASE_TICKS_REQUIRED), Config.maxPumpOperations());
        FloatingLong energyPerTick = energyContainer.getEnergyPerTick();
        for (int i = 1; i < operations; i++) {
            // the tank must have room for another operation, and there must be energy for it
            if (!pump.fluidTank.isEmpty() && FluidAttributes.BUCKET_VOLUME > pump.fluidTank.getNeeded()) {
                break;
            }
            if (!energyContainer.extract(energyPerTick, Action.SIMULATE, AutomationType.INTERNAL).equals(energyPerTick)) {
                break;
            }
            energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
            if (!suck()) {
                break;
            }
        }
        return true;
    }
}
