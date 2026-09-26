package dev.felnull.mekanismtweaks.mixin;

import mekanism.api.recipes.cache.CachedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;

@Mixin(CachedRecipe.class)
public abstract class MixinCachedRecipe {

    @Shadow
    private IntSupplier requiredTicks;

    @Shadow
    public abstract void process();

    /**
     * Whether an operation has been finished in the current process call.
     */
    @Unique
    private boolean mekanismtweaks$finished;

    /**
     * Do not perform the extra operations inside the extra operations.
     */
    @Unique
    private boolean mekanismtweaks$injecting;

    @Inject(method = "process", at = @At(value = "INVOKE", target = "Lmekanism/api/recipes/cache/CachedRecipe;finishProcessing(I)V", shift = At.Shift.AFTER))
    private void mekanismtweaks$confirmFinished(CallbackInfo ci) {
        mekanismtweaks$finished = true;
    }

    /**
     * The ticks required of an ordinary recipe machine are zero or negative if it can do several operations in one tick
     * (see MixinMekanismUtils): 1 - ticks operations. The first is done by Mekanism, this performs the rest, as long as they can be done.
     * Each of them uses the energy of one tick, as the process call does.
     */
    @Inject(method = "process", at = @At("TAIL"))
    private void mekanismtweaks$processMore(CallbackInfo ci) {
        if (mekanismtweaks$injecting || !mekanismtweaks$finished) {
            return;
        }
        mekanismtweaks$finished = false;
        int extra = -requiredTicks.getAsInt();
        if (extra <= 0) {
            return;
        }
        mekanismtweaks$injecting = true;
        try {
            for (int i = 0; i < extra; i++) {
                process();
                if (!mekanismtweaks$finished) {
                    break;
                }
                mekanismtweaks$finished = false;
            }
        } finally {
            mekanismtweaks$injecting = false;
            mekanismtweaks$finished = false;
        }
    }
}
