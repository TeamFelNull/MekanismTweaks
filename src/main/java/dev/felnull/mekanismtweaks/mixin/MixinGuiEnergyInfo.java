package dev.felnull.mekanismtweaks.mixin;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.common.recipe.machines.PressurizedRecipe;
import mekanism.common.tile.*;
import mekanism.common.tile.prefab.TileEntityElectricBlock;
import mekanism.common.tile.prefab.TileEntityMachine;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(value = GuiEnergyInfo.class, remap = false)
public abstract class MixinGuiEnergyInfo extends GuiElement {

    protected MixinGuiEnergyInfo(ResourceLocation resource, IGuiWrapper gui, ResourceLocation def) {
        super(resource, gui, def);
    }

    @ModifyArg(method = "renderForeground", at = @At(value = "INVOKE", target = "Lmekanism/client/gui/element/GuiEnergyInfo;displayTooltips(Ljava/util/List;II)V", ordinal = 0))
    private List<String> warnInsufficientBuffer(List<String> info) {
        if ((guiObj instanceof GuiMekanismTile)) {
            TileEntity tile = ((GuiMekanismTile<?>) guiObj).getTileEntity();
            if (tile instanceof TileEntityElectricBlock && !(tile instanceof TileEntityDigitalMiner)) {
                double required = Double.NaN;
                if (tile instanceof TileEntityPRC) {
                    TileEntityPRC prc = (TileEntityPRC) tile;
                    PressurizedRecipe recipe = prc.getRecipe();
                    required = MekanismUtils.getEnergyPerTick(prc, prc.BASE_ENERGY_PER_TICK + (recipe == null ? 0 : recipe.extraEnergy));
                } else if (tile instanceof TileEntityMachine) required = ((TileEntityMachine) tile).energyPerTick;
                else if (tile instanceof TileEntityElectricPump) required = ((TileEntityElectricPump) tile).energyPerTick;
                else if (tile instanceof TileEntityFluidicPlenisher) required = ((TileEntityFluidicPlenisher) tile).energyPerTick;
                else if (tile instanceof TileEntityFormulaicAssemblicator) required = ((TileEntityFormulaicAssemblicator) tile).energyPerTick;

                if (required > ((TileEntityElectricBlock) tile).getMaxEnergy())
                    info.add(TextFormatting.RED + LangUtils.localize("mekanism.gui.insufficientbuffer"));
            }
        }
        return info;
    }
}
