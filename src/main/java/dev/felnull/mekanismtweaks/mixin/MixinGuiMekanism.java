package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.IExtraWarnings;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.common.inventory.warning.IWarningTracker;
import mekanism.common.inventory.warning.WarningTracker;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMekanism.class, remap = false)
public abstract class MixinGuiMekanism {

    @Shadow
    @Nullable
    private IWarningTracker warningTracker;

    /**
     * Show a warning in the warning tab (the yellow triangle) of every machine GUI if there are too many more Speed Upgrades than Energy Upgrades.
     * This runs right after the GUI elements have been added, and before Mekanism decides whether to add the warning tab.
     */
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lmekanism/client/gui/GuiMekanism;addGuiElements()V", shift = At.Shift.AFTER))
    private void mekanismtweaks$warnShortOfEnergy(CallbackInfo ci) {
        if (!((Object) this instanceof GuiMekanismTile<?, ?> gui)) {
            return;
        }
        TileEntityMekanism tile = gui.getTileEntity();
        if (!tile.supportsUpgrades()) {
            return;
        }
        if (warningTracker == null) {
            warningTracker = new WarningTracker();
        }
        if (warningTracker instanceof IExtraWarnings extra) {
            extra.mekanismtweaks$addExtra(() -> UpgradeEffect.needsEnergyUpgrades(tile), Component.literal("Insert Energy Upgrades!"));
        }
    }
}
