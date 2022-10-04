package dev.felnull.mekanismtweaks.mixin;

import mekanism.common.tile.machine.TileEntityDigitalMiner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TileEntityDigitalMiner.class)
public interface DelayAccessor {
    @Accessor("delay")
    void setDelay(int delay);
}
