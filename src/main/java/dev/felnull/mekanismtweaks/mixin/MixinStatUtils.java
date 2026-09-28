package dev.felnull.mekanismtweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import mekanism.common.util.StatUtils;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
    @WrapMethod(method = "inversePoisson")
    private static int avoidOverflow(double mean, Operation<Integer> original) {
        if (mean >= 1000000) {
            // Cornish-Fisher corrected normal approximation.
            double z = rand.nextGaussian(), sigma = Math.sqrt(mean);
            long sample = Math.round(mean + sigma * z
                    + (z * z - 1) / 6
                    + (z - z * z * z) / (72 * sigma));
            return (int) MathHelper.clamp(sample, 0, Integer.MAX_VALUE);
        }

        int m = (int) mean;
        double p;

        if (m < 2) {
            p = Math.exp(-mean) * (m == 0 ? 1 : mean);
        } else {
            // Corrected Stirling approximation.
            p = Math.exp(m * Math.log1p((mean - m) / m) - (mean - m)
                    - 0.5 * Math.log(2 * Math.PI * m)
                    - 1 / (12.0 * m)
                    + 1 / (360.0 * m * m * m));
        }

        double r = rand.nextDouble();
        double sum = p, left = p, right = p;
        if (r < sum) return m;

        // Accumulate outward from the mode.
        for (int i = 1; i <= 10000; i++) {
            if (i <= m) {
                left *= (m - i + 1) / mean;
                sum += left;
                if (r < sum) return m - i;
            }

            right *= mean / (m + (double) i);
            sum += right;
            if (r < sum) return m + i;
        }

        // Iteration limit reached.
        return m;
    }
}
