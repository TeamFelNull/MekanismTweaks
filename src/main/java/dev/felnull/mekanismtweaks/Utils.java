package dev.felnull.mekanismtweaks;

import mekanism.api.Upgrade;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.interfaces.IUpgradeTile;

public class Utils {

    public static double time(IUpgradeTile tile) {
        return Math.pow(MekanismConfig.general.maxUpgradeMultiplier.get(), tile.getComponent().getUpgrades(Upgrade.SPEED) / -8D);
    }



    public static double electricity(IUpgradeTile tile) {
        int speed = tile.getComponent().getUpgrades(Upgrade.SPEED);
        int energy = tile.getComponent().getUpgrades(Upgrade.ENERGY);
        return Math.pow(MekanismConfig.general.maxUpgradeMultiplier.get(), (2 * speed - Math.min(energy, Math.max(8, speed))) / 8D);
    }

    public static double capacity(IUpgradeTile tile) {
        return Math.pow(MekanismConfig.general.maxUpgradeMultiplier.get(), tile.getComponent().getUpgrades(Upgrade.ENERGY) / 8D);
    }

    public static String exponential(double d) {
        int significant = 4;
        int exp = (int) Math.floor(Math.log10(d));
        d = d * Math.pow(10, -exp);//1.ナニナニ
        d = (double) ((int) Math.round(d * Math.pow(10, significant - 1))) / Math.pow(10, significant - 1);//有効数字で四捨五入
        double dt = (double) ((int) Math.round(d * Math.pow(10, significant - 1))) / Math.pow(10, significant - 1 - exp);//なぜかこれだと誤差なし
        return Math.abs(exp) <= significant - 1 ? dt + "" : d + "E" + exp;
    }
}
