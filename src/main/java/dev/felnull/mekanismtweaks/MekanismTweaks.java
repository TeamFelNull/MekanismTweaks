package dev.felnull.mekanismtweaks;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(MekanismTweaks.MODID)
public class MekanismTweaks {

    public static final String MODID = "mekanismtweaks";

    public MekanismTweaks() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
