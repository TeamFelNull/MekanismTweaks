package dev.felnull.mekanismtweaks;

import mekanism.common.Upgrade;

public class MufflingEffect {

    private static final int ENCODED_FLAG = Integer.MIN_VALUE;
    private static final int MUFFLING_SHIFT = 20;
    private static final int MUFFLING_MASK = 0x3F;
    private static final int VANILLA_DATA_MASK = 0xFFFFF;
    private static final ThreadLocal<ParticleContext> CURRENT_PARTICLE_CONTEXT = new ThreadLocal<>();

    public static int encode(int data, int muffling) {
        return muffling <= 0 ? data : ENCODED_FLAG | (Math.min(muffling - 1, MUFFLING_MASK) << MUFFLING_SHIFT) | (data & VANILLA_DATA_MASK);
    }

    public static boolean isEncoded(int data) {
        return data < 0;
    }

    public static int getMuffling(int data) {
        return isEncoded(data) ? ((data >>> MUFFLING_SHIFT) & MUFFLING_MASK) + 1 : 0;
    }

    public static int getVanillaData(int data) {
        return data & VANILLA_DATA_MASK;
    }

    public static void withParticleContext(int data, Runnable addBlockDestroyEffects) {
        ParticleContext previous = CURRENT_PARTICLE_CONTEXT.get();
        if (isEncoded(data)) CURRENT_PARTICLE_CONTEXT.set(new ParticleContext(data));
        else CURRENT_PARTICLE_CONTEXT.remove();
        try {
            addBlockDestroyEffects.run();
        } finally {
            if (previous == null) CURRENT_PARTICLE_CONTEXT.remove();
            else CURRENT_PARTICLE_CONTEXT.set(previous);
        }
    }

    public static boolean shouldShowParticle() {
        ParticleContext context = CURRENT_PARTICLE_CONTEXT.get();
        return context == null || ((context.nextIndex++ * 37) & 63) < context.keep;// Vanilla uses 64 break particles; 37 permutes them evenly.
    }

    private static final class ParticleContext {
        private final int keep;
        private int nextIndex;

        private ParticleContext(int data) {
            int muffling = getMuffling(data);
            int maximum = Upgrade.MUFFLING.getMax();
            keep = (int) Math.round(64 * Math.pow(1 / 64D, muffling / (maximum - 1D)));
        }
    }
}
