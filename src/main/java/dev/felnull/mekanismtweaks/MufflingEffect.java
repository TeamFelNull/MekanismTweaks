package dev.felnull.mekanismtweaks;

public final class MufflingEffect {

    private static final int ENCODED_FLAG = Integer.MIN_VALUE;
    private static final int SCALE_SHIFT = 20;
    private static final int SCALE_MASK = 0xFF;
    private static final int BLOCK_STATE_MASK = 0xFFFFF;

    private MufflingEffect() {
    }

    public static int encode(int blockState, int installed, int maximum) {
        int remaining = Math.max(0, maximum - installed);
        int scale = maximum > 0 ? Math.round(SCALE_MASK * remaining / (float) maximum) : 0;
        return ENCODED_FLAG | (scale << SCALE_SHIFT) | (blockState & BLOCK_STATE_MASK);
    }

    public static boolean isEncoded(int data) {
        return (data & ENCODED_FLAG) != 0;
    }

    public static float getScale(int data) {
        return (data >>> SCALE_SHIFT & SCALE_MASK) / (float) SCALE_MASK;
    }

    public static int getBlockState(int data) {
        return data & BLOCK_STATE_MASK;
    }
}
