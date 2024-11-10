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
    public static int freeEnergy;

    static {
        File configFile = new File("config/mekanismtweaks.cfg");//load before MekanismItems class to change MaxStackSize
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

    /**
     * Mixin
     */
    public MekanismTweaks() {
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().addConfiguration("mixins.mekanismtweaks.json");
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