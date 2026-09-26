package dev.felnull.mekanismtweaks;

import mekanism.common.IUpgradeManagement;
import mekanism.common.util.MekanismUtils;
import net.minecraft.tileentity.TileEntity;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Static class members for Mixin classes (for they don't allow them)
 */
public class Temp {

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
     * Avoid performing the operation when it cannot operate.
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

    /**
     * Perform excess operations after a machine has operated in its update.
     *
     * @param reqTime ticks required per operation, negative if excess operations are due
     */
    public static void afterUpdate(TileEntity tile, int reqTime, Runnable update) {
        afterUpdate(tile, reqTime, update, Integer.MAX_VALUE);
    }

    /**
     * @param maxExtra the most excess operations performed in one tick
     */
    public static void afterUpdate(TileEntity tile, int reqTime, Runnable update, int maxExtra) {
        if (tile.getWorldObj() == null || tile.getWorldObj().isRemote || isInjecting.get()) return;

        if (!hasOperated.get()) {
            progress.remove(tile);
            return;
        }
        hasOperated.set(false);

        if (reqTime >= 0) {
            progress.remove(tile);
            return;
        }

        Integer stored = progress.get(tile);
        int acc = (stored == null ? 0 : stored) - reqTime;
        isInjecting.set(true);
        try {
            for (int extra = 0; acc >= 20 && extra < maxExtra; extra++) {
                acc -= 20;
                update.run();
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
     * The GUI progress of machines that finish at least one operation per tick.
     *
     * @return null if the default calculation is fine
     */
    public static Double scaledProgress(IUpgradeManagement machine, int ticksRequired, boolean active) {
        return MekanismUtils.getTicks(machine, ticksRequired) <= 1 ? (Double) (active ? 1D : 0D) : null;
    }
}
