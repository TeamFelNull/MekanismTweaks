package dev.felnull.mekanismtweaks;

/**
 * Added by the transformer to the machines that can perform more than one operation within a single tick.
 * Only these machines may get a non-positive ticks required.
 */
public interface IRerun {

    /**
     * Calls onUpdate again to perform an excess operation.
     */
    void mt$rerun();
}
