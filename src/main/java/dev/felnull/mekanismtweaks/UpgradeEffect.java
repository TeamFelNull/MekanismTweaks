package dev.felnull.mekanismtweaks;

import joptsimple.internal.Strings;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.config.MekanismConfig;

import java.text.DecimalFormat;

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
     * Extends the speed effect of Speed Upgrades. When performing multiple operations per tick, excess progress is represented as negative required ticks (-20 per extra operation).
     * Interpolation smooths speed scaling while minimizing significant performance regressions when updating to v1.2.
     *
     * @see <a href="https://www.desmos.com/calculator/fk7mk2t9hq?lang=ja">speed upgrade effect (Desmos)</a>
     */
    public static int speed(IUpgradeTile tile, int def) {
        double speedFraction = fraction(tile, Upgrade.SPEED);
        double ticks = def * effect(-speedFraction);
        if (ticks > 2) return (int) Math.floor(ticks);

        double excess = Math.pow(2, Math.max(inverseEffect(def / 2.) + 1 - speedFraction, 0)) / ticks - 1;
        return (int) -Math.ceil(20 * excess);
    }

    /**
     * Energy to consume.
     * Extend the energy-saving effect of EnergyUpgrade.
     * The energy-saving effect of excess EnergyUpgrades decays based on the SpeedUpgradesInstalled.
     */
    public static double energy(IUpgradeTile tile, double def) {
        return def * effect(2 * fraction(tile, Upgrade.SPEED) - decayedFraction(tile, Upgrade.ENERGY));
    }

    /**
     * Energy buffer amount.
     * If necessary, the energy-buffer increment of excess EnergyUpgrades decays based on the SpeedUpgradesInstalled.
     */
    public static double energyBuffer(IUpgradeTile tile, double def) {
        return def * effect(MekanismTweaks.avoidExcessiveEnergyBuffer ? decayedFraction(tile, Upgrade.ENERGY) : fraction(tile, Upgrade.ENERGY));
    }

    /**
     * Gas to consume. Extend the gas-saving effect of GasUpgrade.
     * The gas-saving effect of excess GasUpgrades decays based on the SpeedUpgradesInstalled.
     */
    public static double gas(IUpgradeTile tile, int def) {
        return def * effect(tile.getComponent().supports(Upgrade.GAS) ? 2 * fraction(tile, Upgrade.SPEED) - decayedFraction(tile, Upgrade.GAS) : fraction(tile, Upgrade.SPEED));
    }

    /**
     * Decayed UpgradesInstalled fraction.
     *
     * @see <a href="https://www.desmos.com/calculator/bc5e1bd598?lang=ja">decaying effect (Desmos)</a>
     */
    public static double decayedFraction(IUpgradeTile tile, Upgrade upgrade) {
        if (upgrade != Upgrade.ENERGY && upgrade != Upgrade.GAS) return fraction(tile, upgrade);

        int m = tile.getComponent().getUpgrades(Upgrade.SPEED);
        int f = upgrade == Upgrade.ENERGY ? MekanismTweaks.freeEnergy : MekanismTweaks.freeGas;
        int n = Math.max(m, f); // non-decay limit
        int x = tile.getComponent().getUpgrades(upgrade);
        double s = upgrade == Upgrade.ENERGY ? MekanismTweaks.sustEnergy : MekanismTweaks.sustGas;

        double sustainRate = s == 0 ? 0 : 1 / Math.max(1, Math.pow(n, Math.log(1 / s) / Math.log(2)) - 1);
        return (x <= n ? x : n + (x - n) * sustainRate) / 8;
    }

    /**
     * Convert the UpgradesInstalled fraction into an effect value.
     */
    public static double effect(double fraction) {
        return Math.pow(MekanismConfig.current().general.maxUpgradeMultiplier.val(), fraction);
    }

    /**
     * Convert an effect multiplier back into an UpgradesInstalled fraction.
     */
    public static double inverseEffect(double effect) {
        return Math.log(effect) / Math.log(MekanismConfig.current().general.maxUpgradeMultiplier.val());
    }

    /**
     * Appropriate exponential notation.
     */
    public static String exponential(double d) {
        int exp = d == 0 ? 0 : (int) Math.floor(Math.log10(Math.abs(d)));
        if (Math.abs(exp) > 3) return new DecimalFormat("0.000E0").format(d);
        return new DecimalFormat(exp >= 3 ? "0" : "0." + "000".substring(Math.max(exp, 0))).format(d);
    }
}
