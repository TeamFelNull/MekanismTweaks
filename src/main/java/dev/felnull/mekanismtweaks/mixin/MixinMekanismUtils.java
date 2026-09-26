package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.IExcess;
import dev.felnull.mekanismtweaks.Temp;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.common.IUpgradeManagement;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MekanismUtils.class, remap = false)
public class MixinMekanismUtils {

    /**
     * Only machines that handle excess operations may get a non-positive ticks required.
     */
    @Inject(method = "getTicks", at = @At("HEAD"), cancellable = true)
    private static void speed(IUpgradeManagement mgmt, int def, CallbackInfoReturnable<Integer> cir) {
        int ticks = UpgradeEffect.speed(mgmt.getSpeedMultiplier(), def);
        cir.setReturnValue(mgmt instanceof IExcess ? ticks : Math.max(1, ticks));
    }

    @Inject(method = "getEnergyPerTick", at = @At("HEAD"), cancellable = true)
    private static void energy(IUpgradeManagement mgmt, double def, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(Temp.isInjecting.get() ? 0 : UpgradeEffect.energy(mgmt.getSpeedMultiplier(), mgmt.getEnergyMultiplier(), def));
    }

    @Inject(method = "getSecondaryEnergyPerTickMean", at = @At("HEAD"), cancellable = true)
    private static void gas(IUpgradeManagement mgmt, int def, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(Temp.isInjecting.get() ? 0 : UpgradeEffect.secondary(mgmt.getSpeedMultiplier(), mgmt.getEnergyMultiplier(), def));
    }

    @Inject(method = "getMaxEnergy(Lmekanism/common/IUpgradeManagement;D)D", at = @At("HEAD"), cancellable = true)
    private static void energyBuffer(IUpgradeManagement mgmt, double def, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(UpgradeEffect.energyBuffer(mgmt.getSpeedMultiplier(), mgmt.getEnergyMultiplier(), def));
    }

    @Inject(method = "getMaxEnergy(Lnet/minecraft/item/ItemStack;Lmekanism/common/IUpgradeManagement;D)D", at = @At("HEAD"), cancellable = true)
    private static void itemEnergyBuffer(ItemStack stack, IUpgradeManagement mgmt, double def, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(UpgradeEffect.energyBuffer(mgmt.getSpeedMultiplier(stack), mgmt.getEnergyMultiplier(stack), def));
    }
}
