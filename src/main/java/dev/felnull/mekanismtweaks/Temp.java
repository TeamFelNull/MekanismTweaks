package dev.felnull.mekanismtweaks;

import java.util.function.BiConsumer;

// Variables for Mixin class (for mixin class doesn't allow static variable)
public class Temp {
    public static String name;//remembering upgrade name
    public static final ThreadLocal<Boolean> isInjecting = ThreadLocal.withInitial(() -> false);//not to call injected method while operating injected method
    public static final BiConsumer<Integer, Runnable> inject = (reqTime, process) -> {
        if (!isInjecting.get()) {
            isInjecting.set(true);
            for (int i = reqTime; i < 0; i++)
                process.run();
            isInjecting.set(false);
        }
    };//Called on processes' ends, to process more while reqTime has negative value. For this also called on added processes' ends, uses isInjecting info.
}
