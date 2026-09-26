package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Config;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.api.Upgrade;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.interfaces.IUpgradeTile;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MekanismUtils.class)
public class MixinMekanismUtils {

    /**
     * All of Mekanism's formulas (ticks required, energy per tick, energy capacity, chemical per tick) go through this.
     * Keep the effect of every 8 upgrades, however many can be installed, and let the effect of surplus Energy and Chemical Upgrades decay.
     */
    @Inject(method = "fractionUpgrades", at = @At("HEAD"), cancellable = true)
    private static void mekanismtweaks$fraction(IUpgradeTile tile, Upgrade type, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(UpgradeEffect.fraction(tile, type));
    }

    /**
     * The ticks required for one operation.
     * Ordinary recipe machines can do several operations in one tick, which is written as zero or a negative number: 1 - ticks is the number of operations.
     * Everything else keeps at least one tick, as Mekanism does not expect anything less.
     */
    @Inject(method = "getTicks", at = @At("HEAD"), cancellable = true)
    private static void mekanismtweaks$ticks(IUpgradeTile tile, int def, CallbackInfoReturnable<Integer> cir) {
        if (!tile.supportsUpgrades()) {
            cir.setReturnValue(def);
            return;
        }
        double ticks = UpgradeEffect.ticks(tile, def);
        if (ticks >= 1) {
            cir.setReturnValue((int) Math.min(Integer.MAX_VALUE, ticks));
        } else if (tile instanceof TileEntityProgressMachine<?>) {
            int operations = (int) Math.min(Config.maxMachineOperations(), Math.min(Integer.MAX_VALUE, 1 / ticks));
            cir.setReturnValue(1 - operations);
        } else {
            cir.setReturnValue(1);
        }
    }

    /**
     * The item form of a machine, where only the number of Energy Upgrades is known.
     */
    @Inject(method = "getMaxEnergy(ILmekanism/api/math/FloatingLong;)Lmekanism/api/math/FloatingLong;", at = @At("HEAD"), cancellable = true)
    private static void mekanismtweaks$itemMaxEnergy(int energyUpgrades, FloatingLong def, CallbackInfoReturnable<FloatingLong> cir) {
        cir.setReturnValue(def.multiply(Math.pow(MekanismConfig.general.maxUpgradeMultiplier.get(), UpgradeEffect.itemEnergyFraction(energyUpgrades))));
    }
}
