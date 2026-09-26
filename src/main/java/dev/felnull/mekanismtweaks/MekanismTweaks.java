package dev.felnull.mekanismtweaks;

import cpw.mods.fml.common.Mod;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

@Mod(modid = "mekanismtweaks", name = "MekanismTweaks", version = "1.7.10(7.1.2)-1.2", dependencies = "required-after:Mekanism@[7.1,8.0)")
public class MekanismTweaks {

    /**
     * Config
     */
    public static int maxSpeed;
    public static int maxEnergy;
    public static boolean energyBuffer;
    public static int freeEnergy;
    public static float sustEnergy;
    public static boolean bulkInstall;
    public static int maxMinerOperations;

    static {
        File configFile = new File("config/mekanismtweaks.cfg");//load before ItemMachineUpgrade is constructed to change MaxStackSize
        Configuration config = new Configuration(configFile);
        config.load();
        String category = "general";
        config.setCategoryComment(category,
                "To change the effect per 8 UpgradesInstalled, you can adjust UpgradeModifier in mekanism.cfg.\n" +
                        "Restart after each change, as this determines MaxStackSize of the upgrade items.");
        maxSpeed = config.getInt("maxSpeed", category, 64, 0, Integer.MAX_VALUE, "MaxSpeedUpgradesInstalled. This would also be the MaxStackSize, although it would not exceed 64.\nInteger.MAX_VALUE is treated as unlimited.");
        maxEnergy = config.getInt("maxEnergy", category, 64, 0, Integer.MAX_VALUE, "MaxEnergyUpgradesInstalled. This would also be the MaxStackSize, although it would not exceed 64.\nInteger.MAX_VALUE is treated as unlimited.");
        energyBuffer = config.getBoolean("energyBuffer", category, true, "Avoid excessive energy buffer.");
        freeEnergy = config.getInt("freeEnergy", category, 8, 0, Integer.MAX_VALUE, "The minimum guaranteed amount of EnergyUpgrades that has energy-saving effect without decay.");
        sustEnergy = config.getFloat("sustEnergy", category, .5F, 0, 1,
                "At 1, the effect is fully sustained, just like freeEnergy equals maxEnergy, as per vanilla mekanism. At 0, no effect is sustained, as per version 1.1.\n" +
                        "At 0.5, to overcome SpeedUpgradesInstalled, EnergyUpgradesInstalled more than or equal to its square is required. At 0.25, its cube is required. And so on.\n");
        bulkInstall = config.getBoolean("bulkInstall", category, true, "Install the whole stack of upgrades in the upgrade slot at once, instead of one upgrade per 40 ticks.");
        maxMinerOperations = config.getInt("maxMinerOperations", category, 64, 1, Integer.MAX_VALUE, "The most blocks a Digital Miner mines in one tick, when its Speed Upgrades make it faster than one block per tick. 1 turns this off, as per vanilla Mekanism.");
        config.save();
    }
}
