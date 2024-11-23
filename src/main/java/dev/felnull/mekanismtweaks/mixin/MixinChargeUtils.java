package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Temp;
import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.common.util.ChargeUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChargeUtils.class ,remap = false)
public class MixinChargeUtils {

    @Inject(method = "discharge", at = @At("HEAD"), cancellable = true)
    private static void correctNeeded(int slotID, IStrictEnergyStorage storer, CallbackInfo ci){
        if(Temp.isInjecting.get()) ci.cancel();
    }
}
