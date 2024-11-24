package dev.felnull.mekanismtweaks.mixin.machine;

import dev.felnull.mekanismtweaks.IOperationData;
import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.Upgrade;
import mekanism.common.tile.TileEntityDigitalMiner;
import mekanism.common.tile.component.TileComponentUpgrade;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityDigitalMiner.class, remap = false)
public abstract class Homoo implements IOperationData {

    @Shadow
    public abstract void onUpdate();

    @Shadow
    public abstract int getDelay();

    @Shadow
    public int delay;

    @Shadow
    public int delayLength;

    @Shadow
    public TileComponentUpgrade upgradeComponent;

    @Shadow public abstract TileComponentUpgrade getComponent();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityDigitalMiner;add(Ljava/util/List;)V"))
    private void confirmOperated(CallbackInfo ci) {
        Temp.hasOperated.set(true);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityDigitalMiner;delay:I", opcode = Opcodes.PUTFIELD, ordinal = 1))
    private void modifyOperatingTicksLater(TileEntityDigitalMiner instance, int value) {
        Temp.modifyOperatingTicksLater((IOperationData) (Object) instance, value);
    }

    @Inject(method = "onUpdate", at = @At("TAIL") /*@At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityChemicalDissolutionChamber;canOperate(Lmekanism/common/recipe/machines/DissolutionRecipe;)Z", ordinal = 1, shift = At.Shift.BY, by = -4)*/)
    public void handleExcessOperations(CallbackInfo ci) {
        Temp.inject2(this, this::onUpdate);
    }

    @Inject(method = "getPerTick", at = @At("HEAD"), cancellable = true)
    private void correctEnergyPerTick(CallbackInfoReturnable<Double> cir) {
        if (Temp.isInjecting.get()) {
            cir.setReturnValue(0.);
            cir.cancel();
        }
    }

    @Override
    public int reqTime() {
        return getDelay();
    }

    @Override
    public int opeTime() {
        return getDelay() - delay;
    }

    @Override
    public void setOpeTime(int operatingTicks) {
        delay = getDelay() - operatingTicks;
    }

    @Override
    public void addOpeTime(int opeTime) {
        delay -= opeTime;
    }

    @Override
    public void subOpeTime(int opeTime) {
        delay += opeTime;
    }

    @Override
    public int toOpeTime(int value) {
        return getDelay() - value;
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityDigitalMiner;delay:I", opcode = Opcodes.GETFIELD, ordinal = 2))
    private int operateWithNegativeDelay(TileEntityDigitalMiner instance) {
        return Math.max(instance.delay, 0);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void mufflingUpgrade(CallbackInfo ci) {
        upgradeComponent.setSupported(Upgrade.MUFFLING);
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;func_175718_b(ILnet/minecraft/util/math/BlockPos;I)V"))
    private void mufflerBreakBlockEffects(World instance, int i, BlockPos blockPos, int j) {
        if (getComponent().getUpgrades(Upgrade.MUFFLING) != Upgrade.MUFFLING.getMax())
            instance.playEvent(i, blockPos, j);
    }
}

