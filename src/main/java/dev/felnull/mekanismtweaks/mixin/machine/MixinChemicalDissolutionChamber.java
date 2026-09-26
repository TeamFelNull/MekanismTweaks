package dev.felnull.mekanismtweaks.mixin.machine;

import dev.felnull.mekanismtweaks.IExcess;
import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityChemicalDissolutionChamber;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityChemicalDissolutionChamber.class, remap = false)
public abstract class MixinChemicalDissolutionChamber implements IExcess {

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/recipe/RecipeHandler;getItemToGasOutput(Lnet/minecraft/item/ItemStack;ZLjava/util/HashMap;)Lmekanism/api/gas/GasStack;"))
    private void confirmOperated(CallbackInfo ci) {
        Temp.hasOperated.set(true);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void handleExcessOperations(CallbackInfo ci) {
        TileEntityChemicalDissolutionChamber self = (TileEntityChemicalDissolutionChamber) (Object) this;
        Temp.afterUpdate(self, MekanismUtils.getTicks(self, self.TICKS_REQUIRED), this::onUpdate);
    }
}
