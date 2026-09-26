package dev.felnull.mekanismtweaks;

import codechicken.lib.vec.Rectangle4i;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiUpgradeTab;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.tileentity.TileEntity;

/**
 * Client-only hooks, kept apart from Hooks so that a dedicated server never loads the GUI classes.
 */
public class ClientHooks {

    /**
     * Speed upgrades more than this many more than energy upgrades make the machine consume more energy than it can store.
     */
    private static final int WARN_DIFFERENCE = 10;

    /**
     * Show a blinking yellow warning icon under the redstone control tab of the machine GUI if there are too many more SpeedUpgrades than EnergyUpgrades.
     * Hovering it explains what to do.
     */
    public static void renderWarning(GuiUpgradeTab tab, TileEntity tileEntity, int xAxis, int yAxis) {
        if (!(tileEntity instanceof IUpgradeTile)) return;
        IUpgradeTile tile = (IUpgradeTile) tileEntity;
        if (!tile.getComponent().supports(Upgrade.ENERGY)) return;
        if (tile.getComponent().getUpgrades(Upgrade.SPEED) - tile.getComponent().getUpgrades(Upgrade.ENERGY) <= WARN_DIFFERENCE) return;

        // the bounds relative to the GUI, as the foreground is drawn relative to it
        // the tabs of the right column are stacked with a gap of 2 (upgrades, redstone control), so this is the slot right under the lock
        Rectangle4i bounds = tab.getBounds(0, 0);
        int x = bounds.x, y = bounds.y + 2 * (bounds.h + 2), w = bounds.w, h = bounds.h;

        // frame
        Gui.drawRect(x, y, x + w, y + h, 0xFF373737);
        Gui.drawRect(x + 1, y + 1, x + w - 1, y + h - 1, 0xFFC6C6C6);

        // triangle, blinking between yellow and orange
        int color = System.currentTimeMillis() / 500 % 2 == 0 ? 0xFFFFDD00 : 0xFFFF9900;
        int height = h - 8, top = y + 4, centerX = x + w / 2;
        for (int row = 0; row < height; row++) {
            int half = row * (w / 2 - 2) / height + 1;
            Gui.drawRect(centerX - half, top + row, centerX + half, top + row + 1, 0xFF000000);
            if (half > 1) Gui.drawRect(centerX - half + 1, top + row, centerX + half - 1, top + row + 1, color);
        }
        Gui.drawRect(centerX - w / 2 + 2, top + height, centerX + w / 2 - 2, top + height + 1, 0xFF000000);

        Minecraft.getMinecraft().fontRenderer.drawString("!", centerX - 2, top + height / 2, 0x000000);

        if (xAxis >= x && xAxis <= x + w && yAxis >= y && yAxis <= y + h) {
            Object screen = Minecraft.getMinecraft().currentScreen;
            if (screen instanceof IGuiWrapper) {
                ((IGuiWrapper) screen).displayTooltip("Insert Energy Upgrades!", xAxis, yAxis);
            }
        }
    }
}
