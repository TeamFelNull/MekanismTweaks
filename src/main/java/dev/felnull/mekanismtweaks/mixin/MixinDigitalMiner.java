package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Config;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.api.Upgrade;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityDigitalMiner.class, remap = false)
public abstract class MixinDigitalMiner {

    /**
     * Whether a block has been mined in the current update.
     */
    @Unique
    private boolean mekanismtweaks$mined;

    /**
     * Do not mine more inside the extra updates.
     */
    @Unique
    private boolean mekanismtweaks$injecting;

    @Shadow
    protected abstract void onUpdateServer();

    /**
     * The Digital Miner adds the drops of every block it mines.
     */
    @Inject(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/machine/TileEntityDigitalMiner;add(Ljava/util/List;)V"))
    private void mekanismtweaks$confirmMined(CallbackInfo ci) {
        mekanismtweaks$mined = true;
    }

    /**
     * Mekanism's Digital Miner mines one block per tick at the most, however many Speed Upgrades it has.
     * Let it mine more in the same tick, like the other machines do, when the time per block is below one tick,
     * by updating it again. Every extra block costs the energy of one tick, as an update does.
     */
    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void mekanismtweaks$mineMore(CallbackInfo ci) {
        if (mekanismtweaks$injecting || !mekanismtweaks$mined) {
            return;
        }
        mekanismtweaks$mined = false;
        TileEntityDigitalMiner miner = (TileEntityDigitalMiner) (Object) this;
        int operations = Math.min(UpgradeEffect.operationsPerTick(miner, MekanismConfig.general.minerTicksPerMine.get()), Config.maxMinerOperations());
        mekanismtweaks$injecting = true;
        try {
            for (int i = 1; i < operations; i++) {
                onUpdateServer();
                if (!mekanismtweaks$mined) {
                    break;
                }
                mekanismtweaks$mined = false;
            }
        } finally {
            mekanismtweaks$injecting = false;
            mekanismtweaks$mined = false;
        }
    }

    /**
     * The block break effect of the Digital Miner makes no sound when it is fully muffled.
     */
    @Redirect(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playEvent(ILnet/minecraft/util/math/BlockPos;I)V", remap = true))
    private void mekanismtweaks$muffleBreakEffect(World level, int type, BlockPos pos, int data) {
        TileEntityDigitalMiner miner = (TileEntityDigitalMiner) (Object) this;
        if (miner.getComponent().getUpgrades(Upgrade.MUFFLING) < Upgrade.MUFFLING.getMax()) {
            level.playEvent(type, pos, data);
        }
    }
}
