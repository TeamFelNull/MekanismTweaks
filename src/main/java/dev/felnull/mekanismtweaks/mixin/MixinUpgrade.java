package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Config;
import mekanism.api.Upgrade;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Upgrade.class, remap = false)
public abstract class MixinUpgrade {

    /**
     * Wrap MaxUpgradesInstalled. Mekanism uses it for every check of how many upgrades fit.
     */
    @Inject(method = "getMax", at = @At("HEAD"), cancellable = true)
    private void mekanismtweaks$getMax(CallbackInfoReturnable<Integer> cir) {
        Upgrade upgrade = (Upgrade) (Object) this;
        if (upgrade == Upgrade.SPEED) {
            cir.setReturnValue(Config.maxSpeed());
        } else if (upgrade == Upgrade.ENERGY) {
            cir.setReturnValue(Config.maxEnergy());
        } else if (upgrade == Upgrade.GAS) {
            cir.setReturnValue(Config.maxGas());
        }
    }

    /**
     * Upgrade.buildMap reads the upgrades from NBT (broken machine item, world load, ...) and clamps them to the field maxStack itself,
     * bypassing getMax. Without this, more than 8 Speed / Energy Upgrades fall back to 8 when the machine is picked up or the world is reloaded.
     */
    @Redirect(method = "buildMap", at = @At(value = "FIELD", target = "Lmekanism/api/Upgrade;maxStack:I", opcode = Opcodes.GETFIELD))
    private static int mekanismtweaks$buildMapMax(Upgrade upgrade) {
        return upgrade.getMax();
    }
}
