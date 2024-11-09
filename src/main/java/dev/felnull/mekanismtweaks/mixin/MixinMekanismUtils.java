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
     * @reason translate multiple operations / tick as minus
     */
    @Overwrite
    public static int getTicks(IUpgradeTile tile, int def) {
        double d = def * Utils.time(tile);
        return d >= 1 ? Utils.clampToInt(d) : -Utils.clampToInt(1 / d);
    }

    /**
     * @author nin8995
     * @reason extension and energy upgrade can affect like before till 8, after that cannot affect more than speed upgrade
     */
    @Overwrite
    public static double getEnergyPerTick(IUpgradeTile tile, double def) {
        return def * Utils.electricity(tile);
    }

    /**
     * @author nin8995
     * @reason extension
     */
    @Overwrite
    public static double getMaxEnergy(IUpgradeTile tile, double def) {
        return def * Utils.capacity(tile);
    }
}
