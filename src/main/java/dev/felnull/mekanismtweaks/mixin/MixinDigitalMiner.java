package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Config;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.common.capabilities.energy.MinerEnergyContainer;
import mekanism.common.config.MekanismConfig;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.BitSet;

@Mixin(TileEntityDigitalMiner.class)
public abstract class MixinDigitalMiner {

    @Shadow
    private boolean hasOverflow;

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
    private void mekanismtweaks$mineMore(CallbackInfoReturnable<Boolean> cir) {
        TileEntityDigitalMiner miner = (TileEntityDigitalMiner) (Object) this;
        int operations = Math.min(MekanismUtils.getOperationsPerTick(miner, MekanismConfig.general.minerTicksPerMine.get(), 1), Config.maxMinerOperations());
        if (operations <= 1) {
            return;
        }
        long energyPerTick = energyContainer.getEnergyPerTick();
        for (int i = 1; i < operations; i++) {
            if (hasOverflow || oresToMine.isEmpty()) {
                break;
            }
            if (energyContainer.extract(energyPerTick, Action.SIMULATE, AutomationType.INTERNAL) != energyPerTick) {
                break;
            }
            energyContainer.extract(energyPerTick, Action.EXECUTE, AutomationType.INTERNAL);
            tryMineBlock();
        }
    }
}
