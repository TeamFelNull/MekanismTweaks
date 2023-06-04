package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityFormulaicAssemblicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityFormulaicAssemblicator.class, remap = false)
public abstract class MixinFormulaicAssemblicator {

    @Shadow
    public int ticksRequired;

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityFormulaicAssemblicator;doSingleCraft()Z", shift = At.Shift.AFTER))
    public void injected(CallbackInfo ci) {
        Temp.inject.accept(ticksRequired, this::onUpdate);
    }
}
