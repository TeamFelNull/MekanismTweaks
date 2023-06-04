package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityDigitalMiner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityDigitalMiner.class, remap = false)
public abstract class MixinHomoo {

    @Shadow
    public int delayLength;

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate",
            at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityDigitalMiner;getDelay()I"))
    public void injected(CallbackInfo ci) {
        Temp.inject.accept(delayLength, this::onUpdate);
    }

    /**
     * @author nin8995
     * @reason Digital Miner cannot work if delay < 0
     */
    @Overwrite
    public int getDelay() {
        return Math.max(this.delayLength, 0);
    }
}

