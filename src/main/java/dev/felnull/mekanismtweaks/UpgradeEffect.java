package dev.felnull.mekanismtweaks;

import mekanism.api.Upgrade;
import mekanism.common.config.MekanismConfig;
import mekanism.common.base.IUpgradeTile;

public class UpgradeEffect {

    /**
     * Speed upgrades more than this many more than Energy Upgrades make the machine consume more energy than it can store.
     */
    public static final int WARN_DIFFERENCE = 10;

    /**
     * The UpgradesInstalled fraction that Mekanism's formulas turn into effects (energy per tick, ticks required, energy capacity, ...).
     * Eight upgrades are one unit of effect for Speed, Energy and Gas Upgrades, as per vanilla mekanism, however many of them can be installed.
     * The effect of Energy and Gas Upgrades beyond the Speed Upgrades decays.
     */
    public static double fraction(IUpgradeTile tile, Upgrade type) {
        if (!tile.getComponent().supports(type)) {
            return 0;
        }
        int count = tile.getComponent().getUpgrades(type);
        int speed = tile.getComponent().getUpgrades(Upgrade.SPEED);
        if (type == Upgrade.SPEED) {
            return count / 8D;
        } else if (type == Upgrade.ENERGY) {
            return decayed(speed, count, Config.freeEnergy(), Config.sustEnergy());
        } else if (type == Upgrade.GAS) {
            return decayed(speed, count, Config.freeGas(), Config.sustGas());
        }
        return count / (double) type.getMax();
    }

    /**
     * The fraction of the Energy Upgrades of an item form of a machine, where the Speed Upgrades are not known.
     */
    public static double itemEnergyFraction(int energyUpgrades) {
        return decayed(0, energyUpgrades, Config.freeEnergy(), Config.sustEnergy());
    }

    /**
     * Decayed UpgradesInstalled fraction.
     * Up to the Speed Upgrades (or the free amount) the upgrades take full effect, the surplus decays based on the Speed Upgrades.
     *
     * @param speed   Speed Upgrades installed
     * @param count   upgrades installed
     * @param free    the minimum guaranteed amount that has effect without decay
     * @param sustain how much of the effect of the surplus is sustained
     */
    public static double decayed(int speed, int count, int free, double sustain) {
        int n = Math.max(speed, free);
        if (sustain == 0) {
            return Math.min(count, n) / 8D;
        }
        double effective = count <= n ? count : n + (count - n) / Math.max(1, Math.pow(speed, Math.log(1 / sustain) / Math.log(2)) - 1);
        return effective / 8D;
    }

    /**
     * Convert the UpgradesInstalled fraction into an effect value.
     */
    public static double effect(double fraction) {
        return Math.pow(MekanismConfig.general.maxUpgradeMultiplier.get(), fraction);
    }

    /**
     * Whether the machine has too many more Speed Upgrades than Energy Upgrades.
     */
    public static boolean needsEnergyUpgrades(IUpgradeTile tile) {
        return tile.getComponent().supports(Upgrade.ENERGY)
                && tile.getComponent().getUpgrades(Upgrade.SPEED) - tile.getComponent().getUpgrades(Upgrade.ENERGY) > WARN_DIFFERENCE;
    }

    /**
     * The time per operation in ticks, as a fraction: below one means that the machine could do several operations per tick.
     */
    public static double ticks(IUpgradeTile tile, int baseTicks) {
        return baseTicks * effect(-fraction(tile, Upgrade.SPEED));
    }

    /**
     * How many operations a machine could do in one tick. Fractional operations are ignored.
     */
    public static int operationsPerTick(IUpgradeTile tile, int baseTicks) {
        double ticks = ticks(tile, baseTicks);
        return ticks >= 1 ? 1 : (int) Math.min(Integer.MAX_VALUE, 1 / ticks);
    }

    /**
     * Appropriate exponential notation.
     */
    public static String exponential(double d) {
        if (d <= 0 || Double.isNaN(d) || Double.isInfinite(d)) {
            return String.valueOf(d);
        }
        int significant = 4;
        int exp = (int) Math.floor(Math.log10(d));
        d = d * Math.pow(10, -exp);
        d = (double) ((int) Math.round(d * Math.pow(10, significant - 1))) / Math.pow(10, significant - 1);
        double dt = (double) ((int) Math.round(d * Math.pow(10, significant - 1))) / Math.pow(10, significant - 1 - exp);
        return Math.abs(exp) <= significant - 1 ? String.valueOf(dt) : d + "E" + exp;
    }
}
