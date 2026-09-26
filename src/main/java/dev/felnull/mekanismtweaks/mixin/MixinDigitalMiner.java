package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Config;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.Upgrade;
import mekanism.common.capabilities.energy.MinerEnergyContainer;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import mekanism.api.math.FloatingLong;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;

@Mixin(value = TileEntityDigitalMiner.class, remap = false)
public abstract class MixinDigitalMiner {

    @Shadow
    private Long2ObjectMap<BitSet> oresToMine;

    @Shadow
    private MinerEnergyContainer energyContainer;

    @Shadow
    private void tryMineBlock() {
    }

    /**
     * Mekanism's Digital Miner mines one block per tick at the most, however many Speed Upgrades it has.
     * Let it mine more in the same tick, like the other machines do, when the time per block is below one tick.
     * Every extra block costs the energy of one tick.
     */
    @Inject(method = "onUpdateServer", at = @At(value = "INVOKE", target = "Lmekanism/common/tile/machine/TileEntityDigitalMiner;tryMineBlock()V", shift = At.Shift.AFTER))
    private void mekanismtweaks$mineMore(CallbackInfo ci) {
        TileEntityDigitalMiner miner = (TileEntityDigitalMiner) (Object) this;
        int operations = Math.min(UpgradeEffect.operationsPerTick(miner, MekanismConfig.general.minerTicksPerMine.get()), Config.maxMinerOperations());
        if (operations <= 1) {
            return;
        }
        FloatingLong energyPerTick = energyContainer.getEnergyPerTick();
        for (int i = 1; i < operations; i++) {
            if (oresToMine.isEmpty()) {
                break;
            }
            if (!energyContainer.extract(energyPerTick, Action.SIMULATE, AutomationType.INTERNAL).equals(energyPerTick)) {
                break;
            }
            energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
            tryMineBlock();
        }
    }

    /**
     * The block break effect of the Digital Miner makes no sound when it is fully muffled.
     */
    @Redirect(method = "tryMineBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;levelEvent(ILnet/minecraft/core/BlockPos;I)V", remap = true))
    private void mekanismtweaks$muffleBreakEffect(Level level, int type, BlockPos pos, int data) {
        TileEntityDigitalMiner miner = (TileEntityDigitalMiner) (Object) this;
        if (miner.getComponent().getUpgrades(Upgrade.MUFFLING) < Upgrade.MUFFLING.getMax()) {
            level.levelEvent(type, pos, data);
        }
    }
}
