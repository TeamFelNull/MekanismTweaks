package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.api.Upgrade;
import mekanism.api.math.MathUtils;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.interfaces.IUpgradeTile;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MekanismUtils.class)
public class MixinMekanismUtils {

    /**
     * All of Mekanism's formulas (ticks required, operations per tick, energy per tick, energy capacity, chemical per tick) go through this.
     * Keep the effect of every 8 upgrades, however many can be installed, and let the effect of surplus Energy and Chemical Upgrades decay.
     */
    @Inject(method = "fractionUpgrades", at = @At("HEAD"), cancellable = true)
    private static void mekanismtweaks$fraction(IUpgradeTile tile, Upgrade type, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(UpgradeEffect.fraction(tile, type));
    }

    /**
     * The item form of a machine, where only the number of Energy Upgrades is known.
     */
    @Inject(method = "getMaxEnergy(IJ)J", at = @At("HEAD"), cancellable = true)
    private static void mekanismtweaks$itemMaxEnergy(int energyUpgrades, long def, CallbackInfoReturnable<Long> cir) {
        cir.setReturnValue(MathUtils.clampToLong(def * Math.pow(MekanismConfig.general.maxUpgradeMultiplier.get(), UpgradeEffect.itemEnergyFraction(energyUpgrades))));
    }
}
