package dev.felnull.mekanismtweaks.mixin.operate;

import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityPRC;
import mekanism.common.tile.prefab.TileEntityOperationalMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityPRC.class, remap = false)
public abstract class PRC {

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityPRC;operate(Lmekanism/common/recipe/machines/PressurizedRecipe;)V", shift = At.Shift.AFTER))
    private void injected(CallbackInfo ci) {
        Temp.inject(((TileEntityOperationalMachine) (Object) this).ticksRequired, this::onUpdate);
    }

}
