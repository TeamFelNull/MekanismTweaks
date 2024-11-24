package dev.felnull.mekanismtweaks.mixin.machine.operational;

import dev.felnull.mekanismtweaks.IOperationData;
import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityAdvancedElectricMachine.class, remap = false)
public abstract class AdvancedElectricMachine {

    @Shadow
    public abstract void onUpdate();

    @Shadow public double secondaryEnergyPerTick;

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/prefab/TileEntityAdvancedElectricMachine;operate(Lmekanism/common/recipe/machines/AdvancedMachineRecipe;)V"))
    private void confirmOperated(CallbackInfo ci) {
        Temp.hasOperated.set(true);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/prefab/TileEntityAdvancedElectricMachine;operatingTicks:I", opcode = Opcodes.PUTFIELD, ordinal = 1))
    private void modifyOperatingTicksLater(TileEntityAdvancedElectricMachine instance, int value) {
        Temp.modifyOperatingTicksLater((IOperationData) (Object) instance, value);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    public void handleExcessOperations(CallbackInfo ci) {
        Temp.inject2((IOperationData) (Object)this, this::onUpdate);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/prefab/TileEntityAdvancedElectricMachine;energyPerTick:D", opcode = Opcodes.GETFIELD))
    private double correctEnergyPerTick(TileEntityAdvancedElectricMachine instance) {
        return Temp.correctEnergyPerTick(instance);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/prefab/TileEntityAdvancedElectricMachine;secondaryEnergyPerTick:D", opcode = Opcodes.GETFIELD))
    private double correctGasPerTick(TileEntityAdvancedElectricMachine instance) {
        return Temp.isInjecting.get() ? 0 : secondaryEnergyPerTick;
    }
}
