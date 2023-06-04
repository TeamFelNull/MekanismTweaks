package dev.felnull.mekanismtweaks.mixin;


import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = TileEntityFactory.class, remap = false)
public abstract class MixinFactory {

    @Shadow
    public abstract void onUpdate();

    @Shadow
    public int ticksRequired;

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityFactory;operate(II)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injected(CallbackInfo ci) {
        Temp.inject.accept(ticksRequired, this::onUpdate);
    }
}