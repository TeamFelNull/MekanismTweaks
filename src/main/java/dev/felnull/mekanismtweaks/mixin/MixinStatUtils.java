package dev.felnull.mekanismtweaks.mixin;

import mekanism.common.util.StatUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = StatUtils.class, remap = false)
public class MixinStatUtils {

    @Shadow
    public static Random rand;

    /**
     * In the default implementation, when the mean is too high like 1000, the upper limit is capped at Infinity, which decrease gas per tick as SpeedUpgradeInstalled.
     */
    @Inject(method = "inversePoisson", at = @At("HEAD"), cancellable = true)
    private static void approximate(double m, CallbackInfoReturnable<Integer> cir) {
        if (m > 256) {
            double d = rand.nextDouble();
            double p = 0;
            int k = -128;
            for (; p < d && k < 3 * m; k++)
                p += Math.pow(m / (m + k), m + k) * Math.exp(k) / Math.sqrt(2 * Math.PI * (m + k));

            int n = (int) m + k;
            cir.setReturnValue(n);
            cir.cancel();
        }
    }
}
