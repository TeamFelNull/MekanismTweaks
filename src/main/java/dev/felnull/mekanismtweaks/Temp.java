package dev.felnull.mekanismtweaks;

import mekanism.common.tile.prefab.TileEntityMachine;

/**
 * Static class members for Mixin classes (for they don't allow them)
 */
public class Temp {

    /**
     * Avoid injecting inside an injection.
     */
    public static final ThreadLocal<Boolean> isInjecting = ThreadLocal.withInitial(() -> false);
    /**
     * Avoid performing the operation when it cannot operate.
     */
    public static final ThreadLocal<Boolean> hasOperated = ThreadLocal.withInitial(() -> false);

    /**
     * Perform excess operations.
     */
    public static void inject(int reqTime, Runnable operation) {
        if (!isInjecting.get()) {
            isInjecting.set(true);
            for (int i = reqTime; i < 0; i++) {
                operation.run();
                if (!hasOperated.get())
                    break;
                else
                    hasOperated.set(false);
            }
            isInjecting.set(false);
        } else {
            hasOperated.set(true);
        }
    }

    /**
     * For Homoo. For she has no apparent operation method.
     */
    public static void injectHomoo(int reqTime, Runnable operation) {
        if (!isInjecting.get()) {
            isInjecting.set(true);
            for (int i = reqTime; i < 0; i++) {
                if (hasOperated.get()) {
                    hasOperated.set(false);
                    operation.run();
                } else {
                    break;
                }
            }
            hasOperated.set(false);
            isInjecting.set(false);
        }
    }

    public static void inject2(IOperationData data, Runnable operation) {
        if (!isInjecting.get() && hasOperated.get()) {
            if (data.reqTime() < 0) {

                // Excessive accumulation in machines that hold opeTime is NG.
                if(data.opeTime() >= 20)
                    data.setOpeTime(data.opeTime() % 20);

                data.addOpeTime(-data.reqTime()); //add excess progress
                isInjecting.set(true);
                while (hasOperated.get() && data.opeTime() >= 20) {
                    hasOperated.set(false);
                    data.subOpeTime(20); // consume 20 progress per operation
                    operation.run();
                }
                data.postProcessing();
                isInjecting.set(false);
            } else if (data.opeTime() > 0) {
                data.setOpeTime(0);
            }
            hasOperated.set(false);
        }
    }

    /**
     * Avoid initializing excess progress.
     */
    public static void modifyOperatingTicksLater(IOperationData data, int value) {
        value = data.toOpeTime(value);
        if (!(value == 0 && data.reqTime() < 0)) data.setOpeTime(value);
    }

    /**
     * Avoid consuming energy while performing excess operations.
     */
    public static double correctEnergyPerTick(TileEntityMachine instance) {
        return isInjecting.get() ? 0 : instance.energyPerTick;
    }
}
