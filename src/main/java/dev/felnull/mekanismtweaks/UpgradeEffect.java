package dev.felnull.mekanismtweaks;

import mekanism.common.Mekanism;

public class UpgradeEffect {

    /**
     * An UpgradesInstalled fraction. Eight upgrades are one "unit" of effect, as per vanilla mekanism.
     */
    public static double fraction(int count) {
        return count / 8D;
    }

    /**
     * Required ticks per operation.
     * Extend the speed effect of SpeedUpgrade. If performing more than one operation within a single tick, treat the excess progress as negative ticks required. (one excess operation per -20 ticks)
     */
    public static int speed(int speed, int def) {
        double ticks = def * effect(-fraction(speed));
        return (int) (ticks > 2 ? ticks : ticks > 1 ? 20 * (ticks - 2) + 1 : -20 / ticks + 1);
    }

    /**
     * Energy to consume.
     * Extend the energy-saving effect of EnergyUpgrade.
     * The energy-saving effect of excess EnergyUpgrades decays based on the SpeedUpgradesInstalled.
     */
    public static double energy(int speed, int energy, double def) {
        return def * effect(2 * fraction(speed) - decayed(speed, energy));
    }

    /**
     * Energy buffer amount.
     * If necessary, the energy-buffer increment of excess EnergyUpgrades decays based on the SpeedUpgradesInstalled.
     */
    public static double energyBuffer(int speed, int energy, double def) {
        return def * effect(MekanismTweaks.energyBuffer ? decayed(speed, energy) : fraction(energy));
    }

    /**
     * Secondary energy (gas) to consume.
     */
    public static double secondary(int speed, int energy, int def) {
        return def * effect(fraction(speed) - decayed(speed, energy));
    }

    /**
     * Decayed EnergyUpgradesInstalled fraction.
     */
    public static double decayed(int speed, int energy) {
        double s = MekanismTweaks.sustEnergy;
        int n = Math.max(speed, MekanismTweaks.freeEnergy);
        if (s == 0) return fraction(Math.min(energy, n));
        return (energy <= n ? energy : n + (energy - n) / Math.max(1, Math.pow(speed, Math.log(1 / s) / Math.log(2)) - 1)) / 8;
    }

    /**
     * Convert the UpgradesInstalled fraction into an effect value.
     */
    public static double effect(double fraction) {
        return Math.pow(Mekanism.maxUpgradeMultiplier, fraction);
    }
}
