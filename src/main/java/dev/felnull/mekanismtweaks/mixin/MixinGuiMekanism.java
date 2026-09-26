package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.IExtraWarnings;
import dev.felnull.mekanismtweaks.UpgradeEffect;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.warning.IWarningTracker;
import mekanism.client.gui.warning.WarningTracker;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.util.text.StringTextComponent;
import javax.annotation.Nullable;
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
        if (!((Object) this instanceof GuiMekanismTile)) {
            return;
        }
        TileEntityMekanism tile = ((GuiMekanismTile<?, ?>) (Object) this).getTileEntity();
        if (!tile.supportsUpgrades()) {
            return;
        }
        if (warningTracker == null) {
            warningTracker = new WarningTracker();
        }
        if (warningTracker instanceof IExtraWarnings) {
            ((IExtraWarnings) warningTracker).mekanismtweaks$addExtra(() -> UpgradeEffect.needsEnergyUpgrades(tile), new StringTextComponent("Insert Energy Upgrades!"));
        }
    }
}
