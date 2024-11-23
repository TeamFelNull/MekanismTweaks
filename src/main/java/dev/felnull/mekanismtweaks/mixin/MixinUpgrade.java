package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.MekanismTweaks;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.util.LangUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = Upgrade.class, remap = false)
public abstract class MixinUpgrade {

    @Shadow
    public abstract boolean canMultiply();

    @Shadow
    private int maxStack;

    /**
     * Wrap MaxStackSize and MaxUpgradesInstalled.
     */
    @Inject(method = "getMax", at = @At("HEAD"), cancellable = true)
    public void getMax(CallbackInfoReturnable<Integer> cir) {
        Upgrade upgrade = (Upgrade) (Object) this;
        cir.setReturnValue(upgrade == Upgrade.SPEED ? MekanismTweaks.maxSpeed :
                           upgrade == Upgrade.ENERGY ? MekanismTweaks.maxEnergy :
                           upgrade == Upgrade.GAS ? MekanismTweaks.maxGas :
                           maxStack);
        cir.cancel();
    }

    /**
     * Display effect cleanly, exponentially. As to saving-effect Upgrade, display saving-effect value.
     */
    @Inject(method = "getMultScaledInfo", at = @At(value = "HEAD"), cancellable = true)
    public void getMultScaledInfo(IUpgradeTile tile, CallbackInfoReturnable<List<String>> cir) {
        List<String> ret = new ArrayList<>();
        Upgrade upgrade = (Upgrade) (Object) this;
        if (this.canMultiply()) {
            double effect = UpgradeEffect.effect(
                    upgrade == Upgrade.ENERGY || upgrade == Upgrade.GAS ? UpgradeEffect.decayed(tile, upgrade) :
                    UpgradeEffect.fraction(tile, upgrade));
            ret.add(LangUtils.localize("gui.upgrades.effect") + ": " + UpgradeEffect.exponential(effect) + "x");
        }

        cir.setReturnValue(ret);
        cir.cancel();
    }
}