package dev.felnull.mekanismtweaks.mixin.machine.operational;

import dev.felnull.mekanismtweaks.IOperationData;
import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.tile.TileEntityPRC;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityPRC.class, remap = false)
public abstract class PRC {

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityPRC;operate(Lmekanism/common/recipe/machines/PressurizedRecipe;)V"))
    private void confirmOperated(CallbackInfo ci) {
        Temp.hasOperated.set(true);
    }

    @Inject(method = "onUpdate", at = @At(value = "TAIL"))
    public void handleExcessOperations(CallbackInfo ci) {
        Temp.inject2((IOperationData)this, this::onUpdate);
    }

    @Redirect(method = "onUpdate", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityPRC;operatingTicks:I", opcode = Opcodes.PUTFIELD, ordinal = 1))
    private void modifyOperatingTicksLater(TileEntityPRC instance, int value) {
        Temp.modifyOperatingTicksLater((IOperationData) (Object)instance, value);
    }

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/util/MekanismUtils;getEnergyPerTick(Lmekanism/common/base/IUpgradeTile;D)D"))
    private double correctEnergyPerTick(IUpgradeTile mgmt, double def) {
        return Temp.isInjecting.get() ? 0 : MekanismUtils.getEnergyPerTick(mgmt, def);
    }
}
