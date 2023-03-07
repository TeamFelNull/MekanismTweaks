package dev.felnull.mekanismtweaks.mixin;


import dev.felnull.mekanismtweaks.Temp;
import mekanism.api.recipes.cache.CachedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.IntSupplier;

@Mixin(value = CachedRecipe.class, remap = false)
public abstract class MixinCachedRecipe {

    @Shadow
    public abstract void process();

    @Shadow
    private IntSupplier requiredTicks;

    @Inject(method = "process", at = @At(value = "INVOKE", target = "Lmekanism/api/recipes/cache/CachedRecipe;finishProcessing(I)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void injected(CallbackInfo ci, int operations) {
        Temp.inject.accept(requiredTicks.getAsInt(), this::process);
    }
}
