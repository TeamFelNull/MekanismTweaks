package dev.felnull.mekanismtweaks.mixin.machine;

import mekanism.client.gui.GuiFormulaicAssemblicator;
import mekanism.common.tile.TileEntityFormulaicAssemblicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiFormulaicAssemblicator.class, remap = false)
public class FormulaicAssemblicatorGui {

    @Redirect(method = "drawGuiContainerBackgroundLayer", at = @At(value = "FIELD", target = "Lmekanism/common/tile/TileEntityFormulaicAssemblicator;ticksRequired:I"))
    private int toMakeScaledOpeTime(TileEntityFormulaicAssemblicator instance) {
        return instance.ticksRequired < 0 ? 20 : instance.ticksRequired;
    }
}
