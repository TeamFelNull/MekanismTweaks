package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityChanceMachine;
import mekanism.common.tile.prefab.TileEntityOperationalMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = TileEntityChanceMachine.class, remap = false)
public abstract class MixinChanceMachine {

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityChanceMachine;operate(Lmekanism/common/recipe/machines/ChanceMachineRecipe;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injected(CallbackInfo ci) {
        Temp.inject.accept(((TileEntityOperationalMachine) (Object) this).ticksRequired, this::onUpdate);
    }
}