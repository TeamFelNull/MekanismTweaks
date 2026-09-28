package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.MekanismTweaks;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = Upgrade.class, remap = false)
public abstract class MixinUpgrade {

    /**
     * Wrap MaxStackSize and MaxUpgradesInstalled.
     */
    @Inject(method = "getMax", at = @At("RETURN"), cancellable = true)
    public void getMax(CallbackInfoReturnable<Integer> cir) {
        Upgrade upgrade = (Upgrade) (Object) this;
        cir.setReturnValue(upgrade == Upgrade.SPEED ? MekanismTweaks.maxSpeed :
                           upgrade == Upgrade.ENERGY ? MekanismTweaks.maxEnergy :
                           upgrade == Upgrade.GAS ? MekanismTweaks.maxGas :
                           upgrade == Upgrade.MUFFLING ? MekanismTweaks.maxMuffling:
                           cir.getReturnValue());
    }

    /**
     * Display effect cleanly, exponentially. As to saving-effect Upgrade, display saving-effect value.
     */
    @ModifyArgs(method = "getMultScaledInfo", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    public void fixEffectInfo(Args args, IUpgradeTile tile) {
        Upgrade upgrade = (Upgrade) (Object) this;
        double effect = UpgradeEffect.effect(
                upgrade == Upgrade.ENERGY || upgrade == Upgrade.GAS ? UpgradeEffect.decayed(tile, upgrade) :
                UpgradeEffect.fraction(tile, upgrade));
        args.set(0, ((String) args.get(0)).replaceFirst("(?<=: ).*(?=x)", UpgradeEffect.exponential(effect)));
    }
}
