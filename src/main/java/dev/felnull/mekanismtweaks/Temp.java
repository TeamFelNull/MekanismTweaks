package dev.felnull.mekanismtweaks;

/**
 * Static class members for Mixin classes (for they don't allow them)
 */
public class Temp {

    /**
     * Not to call injected method while operating injected method.
     */
    public static final ThreadLocal<Boolean> isInjecting = ThreadLocal.withInitial(() -> false);
    /**
     * Not to process when it couldn't process.
     */
    public static final ThreadLocal<Boolean> processed = ThreadLocal.withInitial(() -> false);

    /**
     * Process multiple time.
     */
    public static void inject(int reqTime, Runnable process) {
        if (!isInjecting.get()) {
            isInjecting.set(true);
            for (int i = reqTime; i < 0; i++) {
                process.run();
                if (!processed.get())
                    break;
                else
                    processed.set(false);
            }
            isInjecting.set(false);
        } else {
            processed.set(true);
        }
    }

    /**
     * For Homoo. For she has no apparent operation method.
     */
    public static void injectHomoo(int reqTime, Runnable process) {
        if (!isInjecting.get()) {
            isInjecting.set(true);
            for (int i = reqTime; i < 0; i++) {
                if (processed.get()) {
                    processed.set(false);
                    process.run();
                } else {
                    break;
                }
            }
            isInjecting.set(false);
            processed.set(false);
        }
    }
}
