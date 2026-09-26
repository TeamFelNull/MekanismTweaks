package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Config;
import mekanism.api.Upgrade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Upgrade.class)
public abstract class MixinUpgrade {

    /**
     * Wrap MaxUpgradesInstalled. Mekanism uses it for every check of how many upgrades fit.
     */
    @Inject(method = "getMax", at = @At("HEAD"), cancellable = true)
    private void mekanismtweaks$getMax(CallbackInfoReturnable<Integer> cir) {
        switch ((Upgrade) (Object) this) {
            case SPEED -> cir.setReturnValue(Config.maxSpeed());
            case ENERGY -> cir.setReturnValue(Config.maxEnergy());
            case CHEMICAL -> cir.setReturnValue(Config.maxChemical());
            default -> {
            }
        }
    }
}
