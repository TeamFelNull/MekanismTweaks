package dev.felnull.mekanismtweaks;

import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.config.MekanismConfig;

public class Utils {

    public static double time(IUpgradeTile tile) {
        return effect(-frac(tile, Upgrade.SPEED));
    }

    public static double electricity(IUpgradeTile tile) {
        double speed = frac(tile, Upgrade.SPEED);
        double energy = frac(tile, Upgrade.ENERGY);
        return effect(2 * speed - Math.min(energy, Math.max(Config.freeEnergy, speed)));
    }

    public static double capacity(IUpgradeTile tile) {
        return effect(frac(tile, Upgrade.ENERGY));
    }

    public static double frac(IUpgradeTile tile, Upgrade upgrade) {
        double stack = upgrade == Upgrade.SPEED || upgrade == Upgrade.ENERGY ? 8 : upgrade.getMax();
        return tile.getComponent().getUpgrades(upgrade) / stack;
    }

    public static double effect(double d) {
        return Math.pow(MekanismConfig.current().general.maxUpgradeMultiplier.val(), d);
    }

    public static String exponential(double d) {
        int significant = 4;
        int exp = (int) Math.floor(Math.log10(d));
        d = d * Math.pow(10, -exp);//1.ナニナニ
        d = (double) ((int) Math.round(d * Math.pow(10, significant - 1))) / Math.pow(10, significant - 1);//有効数字で四捨五入
        double dt = (double) ((int) Math.round(d * Math.pow(10, significant - 1))) / Math.pow(10, significant - 1 - exp);//なぜかこれだと誤差なし
        return Math.abs(exp) <= significant - 1 ? String.valueOf(dt) : d + "E" + exp;
    }

    public static int clampToInt(double d) {
        if (d < Integer.MAX_VALUE) {
            return (int) d;
        }
        return Integer.MAX_VALUE;
    }
}
