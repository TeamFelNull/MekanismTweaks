package dev.felnull.mekanismtweaks.mixin.operate;

import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;
import mekanism.common.tile.prefab.TileEntityOperationalMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = TileEntityAdvancedElectricMachine.class, remap = false)
public abstract class AdvancedElectricMachine {

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/prefab/TileEntityAdvancedElectricMachine;operate(Lmekanism/common/recipe/machines/AdvancedMachineRecipe;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injected(CallbackInfo ci) {
        Temp.inject(((TileEntityOperationalMachine) (Object) this).ticksRequired, this::onUpdate);
    }
}
