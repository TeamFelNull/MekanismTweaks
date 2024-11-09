package dev.felnull.mekanismtweaks;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

@Mod(modid = "mekanismtweaks", name = "MekanismTweaks", version = "1.2")
public class Config {

    /**
     * Config
     */
    public static int maxSpeed;
    public static int maxEnergy;
    public static int freeEnergy;

    static {
        File configFile = new File(Minecraft.getMinecraft().gameDir, "config/mekanismtweaks.cfg");
        Configuration config = new Configuration(configFile);
        config.load();
        String category = "general";
        maxSpeed = config.getInt("maxSpeed", category, 64, 0, Short.MAX_VALUE, "Max installations of speed upgrades.");
        maxEnergy = config.getInt("maxEnergy", category, 64, 0,  Short.MAX_VALUE, "Max installations of energy upgrades.");
        freeEnergy = config.getInt("freeEnergy", category, 8, 0,  Short.MAX_VALUE, "Max free installations of energy upgrades. More installation will require equal or greater speed upgrades, though energy buffer increases regardless.");
        config.save();
    }
}

