package dev.felnull.mekanismtweaks.mixin;


import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.machine.TileEntityFluidicPlenisher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityFluidicPlenisher.class, remap = false)
public abstract class MixinFluidicPlenisher {

    @Shadow public int ticksRequired;

    @Shadow protected abstract void onUpdateServer();

    @Inject(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/machine/TileEntityFluidicPlenisher;doPlenish()V", shift = At.Shift.AFTER))
    public void injected(CallbackInfo ci) {
        if(!Temp.isInjectingToFluidicPlenisher) {
            Temp.isInjectingToFluidicPlenisher = true;
            int i = ticksRequired;
            for (; i < 0; i++) {
                onUpdateServer();
            }
            Temp.isInjectingToFluidicPlenisher = false;
        }
    }
}
