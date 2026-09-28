package dev.felnull.mekanismtweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.felnull.mekanismtweaks.MufflingEffect;
import mekanism.common.Upgrade;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

    @ModifyArg(method = "playEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getStateById(I)Lnet/minecraft/block/state/IBlockState;"), index = 0)
    private int unmaskBreakSoundState(int data) {
        return MufflingEffect.isEncoded(data) ? MufflingEffect.getVanillaData(data) : data;
    }

    @ModifyArg(method = "playEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;playSound(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FFZ)V"), index = 3,
                slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getBlockById(I)Lnet/minecraft/block/Block;"),
                                 to = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addBlockDestroyEffects(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V")))
    private float scaleBreakSound(float volume, @Local(argsOnly = true, ordinal = 1) int data) {
        return volume * (MufflingEffect.isEncoded(data) ? Math.max(0, 1 - (float) MufflingEffect.getMuffling(data) / Upgrade.MUFFLING.getMax()) : 1);
    }

    @WrapOperation(method = "playEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addBlockDestroyEffects(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V"))
    private void scaleBreakParticles(ParticleManager instance, BlockPos pos, IBlockState state,
                                     Operation<Void> original, @Local(argsOnly = true, ordinal = 1) int data) {
        MufflingEffect.withParticleContext(data, () -> original.call(instance, pos, state));
    }
}
