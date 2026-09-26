package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.api.Upgrade;
import mekanism.common.MekanismLang;
import mekanism.common.tile.interfaces.IUpgradeTile;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(UpgradeUtils.class)
public class MixinUpgradeUtils {

    /**
     * Display the effect that is really applied, exponentially. As to Energy and Chemical Upgrades, the decayed effect.
     */
    @Inject(method = "getMultScaledInfo", at = @At("HEAD"), cancellable = true)
    private static void mekanismtweaks$multScaledInfo(IUpgradeTile tile, Upgrade upgrade, CallbackInfoReturnable<List<Component>> cir) {
        List<Component> ret = new ArrayList<>();
        if (tile.supportsUpgrades() && upgrade.getMax() > 1) {
            double effect = UpgradeEffect.effect(MekanismUtils.fractionUpgrades(tile, upgrade));
            ret.add(MekanismLang.UPGRADES_EFFECT.translate(UpgradeEffect.exponential(effect)));
        }
        cir.setReturnValue(ret);
    }
}
