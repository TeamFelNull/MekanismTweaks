package dev.felnull.mekanismtweaks.mixin;

import mekanism.api.Upgrade;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityDigitalMiner.class, remap = false)
public abstract class MixinHomoo {

    @Shadow
    private int delay;

    @Shadow
    protected abstract void tryMineBlock();

    @Shadow
    public abstract void recalculateUpgrades(Upgrade upgrade);

    @Shadow
    public abstract int getDelay();

    @Inject(method = "onUpdateServer",
            at = @At(value = "INVOKE", target = "Lmekanism/common/capabilities/energy/MinerEnergyContainer;extract(Lmekanism/api/math/FloatingLong;Lmekanism/api/Action;Lmekanism/api/AutomationType;)Lmekanism/api/math/FloatingLong;", shift = At.Shift.AFTER),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lmekanism/common/tile/machine/TileEntityDigitalMiner;setActive(Z)V")))
    public void injected(CallbackInfo ci) {
        if (delay < 0) {
            for (var i = delay - 1; i < 0; i++)
                tryMineBlock();
            recalculateUpgrades(Upgrade.SPEED);
            ((DelayAccessor) this).setDelay(getDelay());
        }
    }
}

