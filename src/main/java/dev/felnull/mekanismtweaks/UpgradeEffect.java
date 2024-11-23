package dev.felnull.mekanismtweaks;

import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.config.MekanismConfig;

public class UpgradeEffect {

    /**
     * An UpgradesInstalled fraction.
     * Extend the simple effects of upgrades.
     */
    public static float fraction(IUpgradeTile tile, Upgrade upgrade) {
        float defaultMax = upgrade == Upgrade.SPEED || upgrade == Upgrade.ENERGY || upgrade == Upgrade.GAS ? 8 : upgrade.getMax();
        return tile.getComponent().getUpgrades(upgrade) / defaultMax;
    }

    /**
     * Required ticks per operation.
     * Extend the speed effect of SpeedUpgrade. If performing more than one operation within a single tick, treat the excess progress as negative ticks required. (one excess operation per -20 ticks)
     * As smoothing the speed, appropriate interpolation has been applied to ensure that the speed does not decrease when updated to 1.2.
     */
    public static int speed(IUpgradeTile tile, int def) {
        double ticks = def * effect(-fraction(tile, Upgrade.SPEED));
        return (int) (ticks > 2 ? ticks : ticks > 1 ? 20 * (ticks - 2) + 1 : -20 / ticks + 1);
    }

    /**
     * Energy to consume.
     * Extend the energy-saving effect of EnergyUpgrade.
     * The energy-saving effect of excess EnergyUpgrades decays based on the SpeedUpgradesInstalled.
     */
    public static double energy(IUpgradeTile tile, double def) {
        return def * effect(2 * fraction(tile, Upgrade.SPEED) - decayed(tile, Upgrade.ENERGY));
    }

    /**
     * Energy buffer amount.
     * If necessary, the energy-buffer increment of excess EnergyUpgrades decays based on the SpeedUpgradesInstalled.
     */
    public static double energyBuffer(IUpgradeTile tile, double def) {
        return def * effect(MekanismTweaks.energyBuffer ? decayed(tile, Upgrade.ENERGY) : fraction(tile, Upgrade.ENERGY));
    }

    /**
     * Gas to consume. Extend the gas-saving effect of GasUpgrade.
     * The gas-saving effect of excess GasUpgrades decays based on the SpeedUpgradesInstalled.
     */
    public static double gas(IUpgradeTile tile, int def) {
        if (tile.getComponent().supports(Upgrade.GAS))
            return def * effect(2 * fraction(tile, Upgrade.SPEED) - decayed(tile, Upgrade.GAS));
        else
            return def * effect(fraction(tile, Upgrade.SPEED));
    }

    /**
     * Decayed UpgradesInstalled fraction.
     */
    public static double decayed(IUpgradeTile tile, Upgrade upgrade) {
        int m = tile.getComponent().getUpgrades(Upgrade.SPEED);
        int x = tile.getComponent().getUpgrades(upgrade);
        double s = upgrade == Upgrade.ENERGY ? MekanismTweaks.sustEnergy :
                   upgrade == Upgrade.GAS ? MekanismTweaks.sustGas : 0;
        int f = upgrade == Upgrade.ENERGY ? MekanismTweaks.freeEnergy :
                upgrade == Upgrade.GAS ? MekanismTweaks.freeGas : 0;
        int n = Math.max(m, f);
        if (s == 0) return Math.min(x, n);
        return (x <= n ? x : n + (x - n) / Math.max(1, Math.pow(m, Math.log(1 / s) / Math.log(2)) - 1)) / 8;
    }

    /**
     * Convert the UpgradesInstalled fraction into an effect value.
     */
    public static double effect(double fraction) {
        return Math.pow(MekanismConfig.current().general.maxUpgradeMultiplier.val(), fraction);
    }

    /**
     * Appropriate exponential notation.
     */
    public static String exponential(double d) {
        int significant = 4;
        int exp = (int) Math.floor(Math.log10(d));
        d = d * Math.pow(10, -exp);//1.ナニナニ
        d = (double) ((int) Math.round(d * Math.pow(10, significant - 1))) / Math.pow(10, significant - 1);//有効数字で四捨五入
        double dt = (double) ((int) Math.round(d * Math.pow(10, significant - 1))) / Math.pow(10, significant - 1 - exp);//なぜかこれだと誤差なし
        return Math.abs(exp) <= significant - 1 ? String.valueOf(dt) : d + "E" + exp;
    }
}
