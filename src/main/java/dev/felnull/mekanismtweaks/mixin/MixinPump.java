package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.machine.TileEntityElectricPump;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityElectricPump.class, remap = false)
public abstract class MixinPump {

    @Shadow
    public int ticksRequired;

    @Shadow
    protected abstract void onUpdateServer();

    @Inject(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/machine/TileEntityElectricPump;suck()Z", shift = At.Shift.AFTER))
    public void injected(CallbackInfo ci) {
        Temp.inject.accept(ticksRequired, this::onUpdateServer);
    }
}
