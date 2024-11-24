package dev.felnull.mekanismtweaks;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

@Mod(modid = "mekanismtweaks")
@IFMLLoadingPlugin.Name("mekanismtweaks")
public class MekanismTweaks implements IFMLLoadingPlugin {

    /**
     * Config
     */
    public static int maxSpeed;
    public static int maxEnergy;
    public static int maxGas;
    public static int maxMuffling;
    public static boolean energyBuffer;
    public static int freeEnergy;
    public static int freeGas;
    public static float sustEnergy;
    public static float sustGas;

    static {
        File configFile = new File("config/mekanismtweaks.cfg");//load before MekanismItems class to change MaxStackSize
        Configuration config = new Configuration(configFile);
        config.load();
        String category = "general";
        config.setCategoryComment(category,
                "To change the effect per 8 UpgradesInstalled, you can adjust UpgradeModifier in mekanism.cfg.\n" +
                        "Restart after each change, as this determines MaxStackSize of ItemUpgrade.");
        maxSpeed = config.getInt("maxSpeed", category, 64, 0, Integer.MAX_VALUE, "MaxSpeedUpgradesInstalled. This would also be the MaxStackSize, although it would not exceed 64.\nInteger.MAX_VALUE is treated as unlimited.");
        maxEnergy = config.getInt("maxEnergy", category, 64, 0, Integer.MAX_VALUE, "MaxEnergyUpgradesInstalled. This would also be the MaxStackSize, although it would not exceed 64.\nInteger.MAX_VALUE is treated as unlimited.");
        maxGas = config.getInt("maxGas", category, 64, 0, Integer.MAX_VALUE, "Max GasUpgradesInstalled. This would also be the MaxStackSize, although it would not exceed 64.\nInteger.MAX_VALUE is treated as unlimited.");
        maxMuffling = config.getInt("maxMuffling", category, 4, 0, Integer.MAX_VALUE, "Max MufflingUpgradesInstalled. This would also be the MaxStackSize, although it would not exceed 64.\nInteger.MAX_VALUE is treated as unlimited.");
        energyBuffer = config.getBoolean("energyBuffer", category, true, "Avoid excessive energy buffer.");
        freeEnergy = config.getInt("freeEnergy", category, 8, 0, Integer.MAX_VALUE, "The minimum guaranteed amount of EnergyUpgrades that has energy-saving effect without decay.");
        freeGas = config.getInt("freeGas", category, 8, 0, Integer.MAX_VALUE,"The minimum guaranteed amount of GasUpgrades that has gas-saving effect without decay.");
        sustEnergy = config.getFloat("sustEnergy", category, .5F, 0, 1,
                        "At 1, the effect is fully sustained, just like freeEnergy equals maxEnergy, as per vanilla mekanism. At 0, no effect is sustained, as per version 1.1.\n" +
                                "At 0.5, to overcome SpeedUpgradesInstalled, EnergyUpgradesInstalled more than or equal to its square is required. At 0.25, its cube is required. And so on.\n");
        sustGas = config.getFloat("sustGas", category, .5F, 0, 1,
                "At 1, the effect is fully sustained, just like freeGas equals maxGas, as per vanilla mekanism. At 0, no effect is sustained, as per version 1.1.\n" +
                        "At 0.5, to overcome SpeedUpgradesInstalled, GasUpgradesInstalled more than or equal to its square is required. At 0.25, its cube is required. And so on.\n");
        config.save();
    }

    /**
     * Mixin
     */
    public MekanismTweaks() {
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().addConfiguration("mekanismtweaks.mixins.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}