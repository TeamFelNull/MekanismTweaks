package dev.felnull.mekanismtweaks.mixin;

import mekanism.client.gui.GuiElement;
import mekanism.client.gui.GuiUpgradeManagement;
import mekanism.client.gui.IGuiWrapper;
import mekanism.common.IUpgradeTile;
import net.minecraft.client.gui.Gui;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Extends GuiElement (the superclass of the target) only to use its protected members; the constructor is never called.
 */
@Mixin(value = GuiUpgradeManagement.class, remap = false)
public abstract class MixinGuiUpgradeManagement extends GuiElement {

    /**
     * Speed upgrades more than this many more than energy upgrades make the machine consume more energy than it can store.
     */
    private static final int WARN_DIFFERENCE = 10;

    /**
     * The warning icon is a box of this size right below the upgrade panel.
     */
    private static final int ICON_X = 176, ICON_Y = 72, ICON_SIZE = 26;

    @Shadow
    TileEntity tileEntity;

    private MixinGuiUpgradeManagement(ResourceLocation resource, IGuiWrapper gui, ResourceLocation def) {
        super(resource, gui, def);
    }

    /**
     * Show a blinking yellow warning icon below the panel if there are too many more SpeedUpgrades than EnergyUpgrades. Hovering it explains what to do.
     */
    @Inject(method = "renderForeground", at = @At("TAIL"))
    private void warnShortOfEnergy(int xAxis, int yAxis, CallbackInfo ci) {
        IUpgradeTile tile = (IUpgradeTile) tileEntity;
        if (tile.getSpeedMultiplier() - tile.getEnergyMultiplier() <= WARN_DIFFERENCE) return;

        // frame
        Gui.drawRect(ICON_X, ICON_Y, ICON_X + ICON_SIZE, ICON_Y + ICON_SIZE, 0xFF373737);
        Gui.drawRect(ICON_X + 1, ICON_Y + 1, ICON_X + ICON_SIZE - 1, ICON_Y + ICON_SIZE - 1, 0xFFC6C6C6);

        // triangle, blinking between yellow and orange
        int color = System.currentTimeMillis() / 500 % 2 == 0 ? 0xFFFFDD00 : 0xFFFF9900;
        int top = ICON_Y + 4, height = 18, centerX = ICON_X + ICON_SIZE / 2;
        for (int row = 0; row < height; row++) {
            int half = row / 2 + 1;
            Gui.drawRect(centerX - half, top + row, centerX + half, top + row + 1, 0xFF000000);
            if (half > 1) Gui.drawRect(centerX - half + 1, top + row, centerX + half - 1, top + row + 1, color);
        }
        Gui.drawRect(centerX - 4, top + height, centerX + 4, top + height + 1, 0xFF000000);

        if (getFontRenderer() != null) {
            getFontRenderer().drawString("!", centerX - 2, top + 8, 0x000000);
        }

        if (xAxis >= ICON_X && xAxis <= ICON_X + ICON_SIZE && yAxis >= ICON_Y && yAxis <= ICON_Y + ICON_SIZE) {
            displayTooltip("Insert Energy Upgrades!", xAxis, yAxis);
        }
    }
}
