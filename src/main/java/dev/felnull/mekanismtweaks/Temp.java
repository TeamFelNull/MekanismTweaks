package dev.felnull.mekanismtweaks;

import java.util.function.BiConsumer;

// Variables for Mixin class (for mixin class doesn't allow static variable)
public class Temp {
    public static String name;//remembering upgrade name
    public static final ThreadLocal<Boolean> isInjecting = ThreadLocal.withInitial(() -> false);//not to call injected method while operating injected method
    public static final ThreadLocal<Boolean> processed = ThreadLocal.withInitial(() -> false);//not to process when it couldn't process
    public static final BiConsumer<Integer, Runnable> inject = (reqTime, process) -> {
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
        }else{
            processed.set(true);
        }
    };//Called on processes' ends, to process more while reqTime has negative value. For this also called on added processes' ends, uses isInjecting info.

    /**
     * For Homoo.
     */
    public static final BiConsumer<Integer, Runnable> injectHomoo = (reqTime, process) -> {
        if (!isInjecting.get()) {
            isInjecting.set(true);
            for (int i = reqTime; i < 0; i++) {
                if (processed.get()) {
                    processed.set(false);
                    process.run();
                }else {
                    break;
                }
            }
            isInjecting.set(false);
            processed.set(false);
        }
    };
}
