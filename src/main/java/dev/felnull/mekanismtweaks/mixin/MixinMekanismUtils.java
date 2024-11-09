package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Utils;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = MekanismUtils.class, remap = false)
public class MixinMekanismUtils {

    /**
     * @author nin8995
     * @reason Extend speed effect exponentially. If performing more than one operation within a single tick, treat the excess operations as negative ticks required.
     */
    @Overwrite
    public static int getTicks(IUpgradeTile tile, int def) {
        double d = def * Utils.time(tile);
        return Utils.clampToInt(d >= 1 ? d : -1 / d);
    }

    /**
     * @author nin8995
     * @reason Extend energy saving effect exponentially, subject to the freeEnergy limitation.
     */
    @Overwrite
    public static double getEnergyPerTick(IUpgradeTile tile, double def) {
        return def * Utils.electricity(tile);
    }

    /**
     * @author nin8995
     * @reason Extend energy buffer effect exponentially.
     */
    @Overwrite
    public static double getMaxEnergy(IUpgradeTile tile, double def) {
        return def * Utils.capacity(tile);
    }
}
