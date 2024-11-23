package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MekanismUtils.class, remap = false)
public class MixinMekanismUtils {

    @Inject(method = "fractionUpgrades", at = @At("HEAD"), cancellable = true)
    private static void fraction(IUpgradeTile mgmt, Upgrade type, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(UpgradeEffect.fraction(mgmt, type));
        cir.cancel();
    }

    @Inject(method = "getTicks", at = @At("HEAD"), cancellable = true)
    private static void speed(IUpgradeTile mgmt, int def, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(UpgradeEffect.speed(mgmt, def));
        cir.cancel();
    }

    @Inject(method = "getEnergyPerTick", at = @At("HEAD"), cancellable = true)
    private static void energy(IUpgradeTile mgmt, double def, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(UpgradeEffect.energy(mgmt, def));
        cir.cancel();
    }

    @Inject(method = "getMaxEnergy(Lmekanism/common/base/IUpgradeTile;D)D", at = @At("HEAD"), cancellable = true)
    private static void energyBuffer(IUpgradeTile mgmt, double def, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(UpgradeEffect.energyBuffer(mgmt, def));
        cir.cancel();
    }

    @Inject(method = "getSecondaryEnergyPerTickMean", at = @At("HEAD"), cancellable = true)
    private static void gas(IUpgradeTile mgmt, int def, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(UpgradeEffect.gas(mgmt, def));
        cir.cancel();
    }
}
