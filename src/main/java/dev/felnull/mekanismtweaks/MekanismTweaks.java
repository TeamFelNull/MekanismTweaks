package dev.felnull.mekanismtweaks;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(MekanismTweaks.MODID)
public class MekanismTweaks {

    public static final String MODID = "mekanismtweaks";

    public MekanismTweaks(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
