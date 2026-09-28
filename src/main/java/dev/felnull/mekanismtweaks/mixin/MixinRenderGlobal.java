package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.MufflingEffect;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {

    @Shadow
    private WorldClient world;

    @Shadow
    private Minecraft mc;

    @Inject(method = "playEvent", at = @At("HEAD"), cancellable = true)
    private void renderMuffledBreakEffect(EntityPlayer player, int type, BlockPos pos, int data, CallbackInfo ci) {
        if (type != 2001 || !MufflingEffect.isEncoded(data))
            return;

        int blockState = MufflingEffect.getBlockState(data);
        float scale = MufflingEffect.getScale(data);
        Block block = Block.getBlockById(blockState & 4095);
        IBlockState state = block.getStateFromMeta(blockState >> 12 & 255);

        if (scale > 0 && block.getDefaultState().getMaterial() != Material.AIR) {
            SoundType sound = block.getSoundType(Block.getStateById(blockState), world, pos, null);
            world.playSound(pos, sound.getBreakSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1) / 2 * scale, sound.getPitch() * 0.8F, false);
        }

        if (scale > 0 && !block.isAir(state, world, pos) && !block.addDestroyEffects(world, pos, mc.effectRenderer)) {
            state = state.getActualState(world, pos);
            int particles = Math.round(64 * scale);
            for (int i = 0; i < particles; i++) {
                int cell = i * 37 & 63;
                double x = ((cell >> 4) + 0.5) / 4;
                double y = (((cell >> 2) & 3) + 0.5) / 4;
                double z = ((cell & 3) + 0.5) / 4;
                Particle particle = mc.effectRenderer.spawnEffectParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(),
                        pos.getX() + x, pos.getY() + y, pos.getZ() + z,
                        x - 0.5, y - 0.5, z - 0.5, Block.getStateId(state));
                if (particle instanceof ParticleDigging)
                    ((ParticleDigging) particle).setBlockPos(pos);
            }
        }
        ci.cancel();
    }
}
