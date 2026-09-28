package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.MufflingEffect;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class MixinParticleManager {

    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    private void filterBreakParticles(Particle effect, CallbackInfo ci) {
        if (!MufflingEffect.shouldShowParticle()) ci.cancel();
    }
}
