package dev.felnull.mekanismtweaks;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * Config
 */
@Mod(modid = "mekanismtweaks", name = "MekanismTweaks", version = "1.2", dependencies = "required-after:mekanism")
public class MekanismTweaks {

    public static int maxSpeed;
    public static int maxEnergy;
    public static int freeEnergy;

    static {
        File configFile = new File("config/mekanismtweaks.cfg");//load before MekanismItems class is loaded
        Configuration config = new Configuration(configFile);
        config.load();
        String category = "general";
        config.setCategoryComment(category,
                "Restart after each change, as this determines MaxStackSize of ItemUpgrade.\n" +
                        "To change the effect per 8 UpgradesInstalled, you can adjust UpgradeModifier in mekanism.cfg.");
        maxSpeed = config.getInt("maxSpeed", category, 64, 0, Integer.MAX_VALUE, "Max SpeedUpgradesInstalled.");
        maxEnergy = config.getInt("maxEnergy", category, 64, 0, Integer.MAX_VALUE, "Max EnergyUpgradesInstalled.");
        freeEnergy = config.getInt("freeEnergy", category, 8, 0, Integer.MAX_VALUE, "Max free EnergyUpgradesInstalled. Any additional upgrades will require equal or greater SpeedUpgradesInstalled to save energy, although the energy buffer will still increase regardless.");
        config.save();
    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        System.out.println("Am I there?");
    }
}