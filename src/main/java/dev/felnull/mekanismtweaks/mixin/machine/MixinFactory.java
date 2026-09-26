package dev.felnull.mekanismtweaks.mixin.machine;

import dev.felnull.mekanismtweaks.IExcess;
import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityFactory.class, remap = false)
public abstract class MixinFactory implements IExcess {

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "operate", at = @At("HEAD"))
    private void confirmOperated(CallbackInfo ci) {
        Temp.hasOperated.set(true);
    }

    /**
     * The Factory updates all of its processes at once, so the excess operations are performed by updating it again.
     */
    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void handleExcessOperations(CallbackInfo ci) {
        TileEntityFactory self = (TileEntityFactory) (Object) this;
        Temp.afterUpdate(self, MekanismUtils.getTicks(self, self.TICKS_REQUIRED), this::onUpdate);
    }

    /**
     * Display full progress instead of dividing by a non-positive number.
     */
    @Inject(method = "getScaledProgress", at = @At("HEAD"), cancellable = true)
    private void scaledProgress(int i, int process, CallbackInfoReturnable<Integer> cir) {
        TileEntityFactory self = (TileEntityFactory) (Object) this;
        if (MekanismUtils.getTicks(self, self.TICKS_REQUIRED) <= 1) {
            cir.setReturnValue(self.isActive ? i : 0);
        }
    }
}
