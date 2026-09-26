package dev.felnull.mekanismtweaks;

import mekanism.api.Coord4D;
import mekanism.api.Range4D;
import mekanism.common.Mekanism;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeItem;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityBasicMachine;
import mekanism.common.tile.TileEntityChemicalCrystallizer;
import mekanism.common.tile.TileEntityChemicalDissolutionChamber;
import mekanism.common.tile.TileEntityChemicalOxidizer;
import mekanism.common.tile.TileEntityContainerBlock;
import mekanism.common.tile.TileEntityDigitalMiner;
import mekanism.common.tile.TileEntityElectricBlock;
import mekanism.common.tile.TileEntityElectricPump;
import mekanism.common.tile.TileEntityMetallurgicInfuser;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.StatUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Called from the code that the transformer inserts into Mekanism (see MekanismTweaksTransformer).
 * Every method here is referenced by name and descriptor from there.
 */
public class Hooks {

    /**
     * Avoid injecting inside an injection. While injecting, energy and gas are not consumed again.
     */
    public static final ThreadLocal<Boolean> isInjecting = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };
    /**
     * Avoid performing the excess operations when the machine could not operate.
     */
    public static final ThreadLocal<Boolean> hasOperated = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    /**
     * Excess progress of each machine. Twenty progress is one operation.
     */
    private static final Map<TileEntity, Integer> progress = Collections.synchronizedMap(new WeakHashMap<TileEntity, Integer>());

    // ---- MekanismUtils ----

    public static float fractionUpgrades(IUpgradeTile tile, Upgrade upgrade) {
        return UpgradeEffect.fraction(tile, upgrade);
    }

    /**
     * Only machines that handle excess operations may get a non-positive ticks required.
     */
    public static int getTicks(IUpgradeTile tile, int def) {
        int ticks = UpgradeEffect.speed(tile, def);
        // the Digital Miner keeps the ticks as a delay, which must never get non-positive
        return tile instanceof IRerun && !(tile instanceof TileEntityDigitalMiner) ? ticks : Math.max(1, ticks);
    }

    public static double getEnergyPerTick(IUpgradeTile tile, double def) {
        return isInjecting.get() ? 0 : UpgradeEffect.energy(tile, def);
    }

    public static double getSecondaryEnergyPerTickMean(IUpgradeTile tile, int def) {
        return UpgradeEffect.gas(tile, def);
    }

    public static double getMaxEnergy(IUpgradeTile tile, double def) {
        return UpgradeEffect.energyBuffer(tile, def);
    }

    // ---- Upgrade ----

    /**
     * Wrap MaxStackSize and MaxUpgradesInstalled.
     */
    public static int getMax(Upgrade upgrade, int maxStack) {
        return upgrade == Upgrade.SPEED ? MekanismTweaks.maxSpeed :
                upgrade == Upgrade.ENERGY ? MekanismTweaks.maxEnergy :
                        upgrade == Upgrade.GAS ? MekanismTweaks.maxGas :
                                upgrade == Upgrade.MUFFLING ? MekanismTweaks.maxMuffling :
                                        maxStack;
    }

    /**
     * Display effect cleanly, exponentially. As to saving-effect Upgrade, display saving-effect value.
     */
    public static List<String> multScaledInfo(Upgrade upgrade, IUpgradeTile tile) {
        List<String> ret = new ArrayList<String>();
        if (upgrade.canMultiply()) {
            double effect = UpgradeEffect.effect(
                    upgrade == Upgrade.ENERGY || upgrade == Upgrade.GAS ? UpgradeEffect.decayed(tile, upgrade) :
                            UpgradeEffect.fraction(tile, upgrade));
            ret.add(LangUtils.localize("gui.upgrades.effect") + ": " + UpgradeEffect.exponential(effect) + "x");
        }
        return ret;
    }

    // ---- ItemUpgrade ----

    /**
     * Limit MaxStackSize to 64 to avoid unstable item interaction behavior.
     */
    public static int stackSize(int size) {
        return Math.min(size, 64);
    }

    // ---- StatUtils ----

    /**
     * In the default implementation, when the mean is too high like 1000, the upper limit is capped at Infinity, which decrease gas per tick as SpeedUpgradeInstalled.
     */
    public static int inversePoisson(double mean) {
        if (mean > 256) {
            double d = StatUtils.rand.nextDouble();
            double p = 0;
            int k = -128;
            for (; p < d && k < 3 * mean; k++)
                p += Math.pow(mean / (mean + k), mean + k) * Math.exp(k) / Math.sqrt(2 * Math.PI * (mean + k));
            return (int) mean + k;
        }

        double r = StatUtils.rand.nextDouble() * Math.exp(mean);
        int m = 0;
        double p = 1;
        double stirlingValue = mean * Math.E;
        double stirlingCoeff = 1 / Math.sqrt(2 * Math.PI);
        while ((p < r) && (m < 3 * Math.ceil(mean))) {
            m++;
            p += stirlingCoeff / Math.sqrt(m) * Math.pow((stirlingValue / m), m);
        }
        return m;
    }

    // ---- TileComponentUpgrade ----

    /**
     * Install the whole stack in the upgrade slot at once, as many as the maximum allows, instead of one upgrade per UPGRADE_TICKS_REQUIRED.
     */
    public static void installAll(TileComponentUpgrade component) {
        if (!MekanismTweaks.bulkInstall) return;

        TileEntityContainerBlock tile = component.tileEntity;
        World world = tile.getWorldObj();
        if (world == null || world.isRemote) return;

        int slot = component.getUpgradeSlot();
        ItemStack stack = tile.inventory[slot];
        if (stack == null || !(stack.getItem() instanceof IUpgradeItem)) return;

        Upgrade upgrade = ((IUpgradeItem) stack.getItem()).getUpgradeType(stack);
        if (!component.supports(upgrade)) return;

        int count = Math.min(stack.stackSize, upgrade.getMax() - component.getUpgrades(upgrade));
        if (count <= 0) return;

        for (int i = 0; i < count; i++) {
            component.addUpgrade(upgrade);
        }

        stack.stackSize -= count;
        if (stack.stackSize <= 0) tile.inventory[slot] = null;

        component.upgradeTicks = 0;
        Mekanism.packetHandler.sendToReceivers(new TileEntityMessage(Coord4D.get(tile), tile.getNetworkedData(new ArrayList())), new Range4D(Coord4D.get(tile)));
        tile.markDirty();
    }

    // ---- machines ----

    /**
     * Called at the head of every operate method.
     */
    public static void operated() {
        hasOperated.set(true);
    }

    /**
     * Stands in for the pump's calls of suck in its update. The pump has performed an operation only if it has really pumped.
     */
    public static boolean suck(TileEntityElectricPump pump, boolean take) {
        boolean sucked = pump.suck(take);
        if (take && sucked) {
            hasOperated.set(true);
        }
        return sucked;
    }

    /**
     * Avoid consuming energy and gas while performing excess operations.
     */
    public static double guard(double value) {
        return isInjecting.get() ? 0 : value;
    }

    /**
     * Perform excess operations after a machine has operated in its update.
     */
    public static void afterUpdate(Object machine) {
        TileEntity tile = (TileEntity) machine;
        World world = tile.getWorldObj();
        if (world == null || world.isRemote || isInjecting.get()) return;

        if (!hasOperated.get()) {
            progress.remove(tile);
            return;
        }
        hasOperated.set(false);

        int reqTime = ticksRequired(machine);
        if (reqTime >= 0) {
            progress.remove(tile);
            return;
        }

        Integer stored = progress.get(tile);
        int acc = (stored == null ? 0 : stored) - reqTime;
        isInjecting.set(true);
        try {
            int maxExtra = machine instanceof TileEntityDigitalMiner ? MekanismTweaks.maxMinerOperations :
                    machine instanceof TileEntityElectricPump ? MekanismTweaks.maxPumpOperations : Integer.MAX_VALUE;
            for (int extra = 0; acc >= 20 && extra < maxExtra; extra++) {
                acc -= 20;
                ((IRerun) machine).mt$rerun();
                if (!hasOperated.get()) {
                    acc = 0;
                    break;
                }
                hasOperated.set(false);
            }
            // if the cap stopped the loop, do not save up the rest
            acc = Math.min(acc, 19);
        } finally {
            isInjecting.set(false);
            hasOperated.set(false);
        }
        progress.put(tile, acc);
    }

    /**
     * MaxEnergy can now also rely on SpeedUpgrade.
     */
    public static void afterRecalc(Object machine, Upgrade upgrade) {
        if (!MekanismTweaks.energyBuffer || upgrade != Upgrade.SPEED) return;
        if (machine instanceof TileEntityElectricBlock && machine instanceof IUpgradeTile) {
            TileEntityElectricBlock block = (TileEntityElectricBlock) machine;
            block.maxEnergy = MekanismUtils.getMaxEnergy((IUpgradeTile) machine, block.BASE_MAX_ENERGY);
            block.setEnergy(Math.min(block.getMaxEnergy(), block.getEnergy()));
        }
    }

    /**
     * Display full progress instead of a meaningless ratio while performing at least one operation per tick.
     */
    public static double scaledProgress(Object machine) {
        int operating = 0, required = 1;
        boolean active = false;
        if (machine instanceof TileEntityBasicMachine) {
            TileEntityBasicMachine<?, ?, ?> m = (TileEntityBasicMachine<?, ?, ?>) machine;
            operating = m.operatingTicks;
            required = m.ticksRequired;
            active = m.isActive;
        } else if (machine instanceof TileEntityMetallurgicInfuser) {
            TileEntityMetallurgicInfuser m = (TileEntityMetallurgicInfuser) machine;
            operating = m.operatingTicks;
            required = m.ticksRequired;
            active = m.isActive;
        } else if (machine instanceof TileEntityChemicalCrystallizer) {
            TileEntityChemicalCrystallizer m = (TileEntityChemicalCrystallizer) machine;
            operating = m.operatingTicks;
            required = m.ticksRequired;
            active = m.isActive;
        } else if (machine instanceof TileEntityChemicalOxidizer) {
            TileEntityChemicalOxidizer m = (TileEntityChemicalOxidizer) machine;
            operating = m.operatingTicks;
            required = m.ticksRequired;
            active = m.isActive;
        } else if (machine instanceof TileEntityChemicalDissolutionChamber) {
            TileEntityChemicalDissolutionChamber m = (TileEntityChemicalDissolutionChamber) machine;
            operating = m.operatingTicks;
            required = m.ticksRequired;
            active = m.isActive;
        }
        return required <= 1 ? (active ? 1D : 0D) : (double) operating / required;
    }

    private static int ticksRequired(Object machine) {
        if (machine instanceof TileEntityElectricPump) return ((TileEntityElectricPump) machine).ticksRequired;
        if (machine instanceof TileEntityDigitalMiner) {
            TileEntityDigitalMiner miner = (TileEntityDigitalMiner) machine;
            return UpgradeEffect.speed(miner, miner.BASE_DELAY);
        }
        if (machine instanceof TileEntityBasicMachine) return ((TileEntityBasicMachine<?, ?, ?>) machine).ticksRequired;
        if (machine instanceof TileEntityMetallurgicInfuser) return ((TileEntityMetallurgicInfuser) machine).ticksRequired;
        if (machine instanceof TileEntityChemicalCrystallizer) return ((TileEntityChemicalCrystallizer) machine).ticksRequired;
        if (machine instanceof TileEntityChemicalOxidizer) return ((TileEntityChemicalOxidizer) machine).ticksRequired;
        if (machine instanceof TileEntityChemicalDissolutionChamber) return ((TileEntityChemicalDissolutionChamber) machine).ticksRequired;
        return 0;
    }
}
