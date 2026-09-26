package dev.felnull.mekanismtweaks;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * config/mekanismtweaks-common.toml
 * <p>
 * The getters fall back to the defaults if the config has not been loaded yet.
 */
public class Config {

    private static final int DEFAULT_MAX = 64;
    private static final int DEFAULT_FREE = 8;
    private static final double DEFAULT_SUST = 0.5;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue MAX_SPEED = BUILDER
            .comment("Maximum Speed Upgrades installed. Integer.MAX_VALUE is treated as unlimited.",
                    "The effect of every 8 upgrades stays the same (see UpgradeModifier in mekanism-general.toml).")
            .defineInRange("maxSpeed", DEFAULT_MAX, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue MAX_ENERGY = BUILDER
            .comment("Maximum Energy Upgrades installed. Integer.MAX_VALUE is treated as unlimited.")
            .defineInRange("maxEnergy", DEFAULT_MAX, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue MAX_CHEMICAL = BUILDER
            .comment("Maximum Chemical Upgrades installed. Integer.MAX_VALUE is treated as unlimited.")
            .defineInRange("maxChemical", DEFAULT_MAX, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue FREE_ENERGY = BUILDER
            .comment("The minimum guaranteed amount of Energy Upgrades that have their effect without decay.")
            .defineInRange("freeEnergy", DEFAULT_FREE, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue FREE_CHEMICAL = BUILDER
            .comment("The minimum guaranteed amount of Chemical Upgrades that have their effect without decay.")
            .defineInRange("freeChemical", DEFAULT_FREE, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue SUST_ENERGY = BUILDER
            .comment("How much of the effect of Energy Upgrades beyond the Speed Upgrades is sustained.",
                    "At 1 nothing decays, as per vanilla Mekanism. At 0 the surplus has no effect.",
                    "At 0.5, to overcome the Speed Upgrades, Energy Upgrades of at least their square are required. At 0.25, their cube. And so on.")
            .defineInRange("sustEnergy", DEFAULT_SUST, 0, 1);
    private static final ModConfigSpec.DoubleValue SUST_CHEMICAL = BUILDER
            .comment("Same as sustEnergy, for the Chemical Upgrades.")
            .defineInRange("sustChemical", DEFAULT_SUST, 0, 1);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static int maxSpeed() {
        return get(MAX_SPEED, DEFAULT_MAX);
    }

    public static int maxEnergy() {
        return get(MAX_ENERGY, DEFAULT_MAX);
    }

    public static int maxChemical() {
        return get(MAX_CHEMICAL, DEFAULT_MAX);
    }

    public static int freeEnergy() {
        return get(FREE_ENERGY, DEFAULT_FREE);
    }

    public static int freeChemical() {
        return get(FREE_CHEMICAL, DEFAULT_FREE);
    }

    public static double sustEnergy() {
        return getDouble(SUST_ENERGY);
    }

    public static double sustChemical() {
        return getDouble(SUST_CHEMICAL);
    }

    private static int get(ModConfigSpec.IntValue value, int def) {
        try {
            return value.get();
        } catch (IllegalStateException e) {
            return def;
        }
    }

    private static double getDouble(ModConfigSpec.DoubleValue value) {
        try {
            return value.get();
        } catch (IllegalStateException e) {
            return DEFAULT_SUST;
        }
    }
}
