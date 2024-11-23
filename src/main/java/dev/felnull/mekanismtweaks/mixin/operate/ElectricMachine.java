package dev.felnull.mekanismtweaks.mixin.operate;


import dev.felnull.mekanismtweaks.IOperationData;
import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityElectricMachine.class, remap = false)
public abstract class ElectricMachine {

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/prefab/TileEntityElectricMachine;operate(Lmekanism/common/recipe/machines/BasicMachineRecipe;)V"))
    private void confirmOperated(CallbackInfo ci) {
        Temp.hasOperated.set(true);
    }

    @Inject(method = "onUpdate", at = @At(value = "TAIL"))
    public void handleExcessOperations(CallbackInfo ci) {
        Temp.inject2((IOperationData)this, this::onUpdate);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/prefab/TileEntityElectricMachine;operatingTicks:I", opcode = Opcodes.PUTFIELD, ordinal = 1))
    private void modifyOperatingTicksLater(TileEntityElectricMachine instance, int value) {
        Temp.modifyOperatingTicksLater((IOperationData) (Object)instance, value);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/prefab/TileEntityElectricMachine;energyPerTick:D", opcode = Opcodes.GETFIELD))
    private double correctEnergyPerTick(TileEntityElectricMachine instance) {
        return Temp.correctEnergyPerTick(instance);
    }
}
