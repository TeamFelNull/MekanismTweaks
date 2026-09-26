package dev.felnull.mekanismtweaks.mixin.machine;

import dev.felnull.mekanismtweaks.MekanismTweaks;
import dev.felnull.mekanismtweaks.Temp;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.common.tile.TileEntityDigitalMiner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mekanism's Digital Miner mines one block per tick at the most, however many Speed Upgrades it has.
 * Let it mine more in the same tick, like the other machines do, when the time per block is below one tick.
 * The miner is not an IExcess, so that the delay it keeps (MekanismUtils.getTicks) never gets non-positive.
 */
@Mixin(value = TileEntityDigitalMiner.class, remap = false)
public abstract class MixinDigitalMiner {

    @Shadow
    public abstract void onUpdate();

    /**
     * Confirm that a block has been mined: the delay is only set after mining one.
     */
    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityDigitalMiner;getDelay()I"))
    private void confirmMined(CallbackInfo ci) {
        Temp.hasOperated.set(true);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void handleExcessMining(CallbackInfo ci) {
        TileEntityDigitalMiner self = (TileEntityDigitalMiner) (Object) this;
        Temp.afterUpdate(self, UpgradeEffect.speed(self.getSpeedMultiplier(), 80), this::onUpdate, MekanismTweaks.maxMinerOperations);
    }
}
