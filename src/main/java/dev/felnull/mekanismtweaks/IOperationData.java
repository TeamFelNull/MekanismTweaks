package dev.felnull.mekanismtweaks;

public interface IOperationData {

    /**
     * i.e. ticksRequired, delayLength
     */
    int reqTime();

    /**
     * i.e. operatingTicks, delay, progress
     */
    int opeTime();

    void setOpeTime(int opeTime);

    default void addOpeTime(int opeTime) {
        setOpeTime(this.opeTime() + opeTime);
    }

    default void subOpeTime(int opeTime) {
        setOpeTime(this.opeTime() - opeTime);
    }


    /**
     * Display excess progress as well.
     */
    default double getScaledOpeTime() {
        return Math.min(1, reqTime() > 0 ? opeTime() / (double) reqTime() : opeTime() / 20D);
    }

    /**
     * Special processing is required for special implementations.
     */
    default void postProcessing() {
    }

    /**
     * If process and the variable that controls process are different. For her.
     */
    default int toOpeTime(int value){
        return value;
    }
}
