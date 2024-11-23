package dev.felnull.mekanismtweaks.mixin.operate;

import dev.felnull.mekanismtweaks.Temp;
import mekanism.common.tile.TileEntityElectricPump;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityElectricPump.class, remap = false)
public abstract class ElectricPump {

    @Shadow
    public int ticksRequired;

    @Shadow
    public abstract void onUpdate();

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/TileEntityElectricPump;suck(Z)Z", shift = At.Shift.AFTER))
    public void injected(CallbackInfo ci) {
        Temp.inject(ticksRequired, this::onUpdate);
    }
}
