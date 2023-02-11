package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Utils;
import mekanism.api.math.FloatingLong;
import mekanism.api.math.MathUtils;
import mekanism.common.tile.interfaces.IUpgradeTile;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = MekanismUtils.class, remap = false)
public class MixinMekanismUtils {

    /**
     * @author nin8995
     * @reason extension and translate multiple operations / tick as minus
     */
    @Overwrite
    public static int getTicks(IUpgradeTile tile, int def) {
        if (tile.supportsUpgrades()) {
            double d = def * Utils.time(tile);
            return d >= 1 ? MathUtils.clampToInt(d) : MathUtils.clampToInt(1 / d) * -1;
        }
        return def;
    }

    /**
     * @author nin8995
     * @reason extension and no energy upgrade effect
     */
    @Overwrite
    public static FloatingLong getEnergyPerTick(IUpgradeTile tile, FloatingLong def) {
        if (tile.supportsUpgrades()) {
            return def.multiply(Utils.electricity(tile));
        }
        return def;
    }

    /**
     * @author nin8995
     * @reason extension
     */
    @Overwrite
    public static FloatingLong getMaxEnergy(IUpgradeTile tile, FloatingLong def) {
        if (tile.supportsUpgrades()) {
            var a = def.copy();
            return def.multiply(Utils.capacity(tile));
        }
        return def;
    }
}
