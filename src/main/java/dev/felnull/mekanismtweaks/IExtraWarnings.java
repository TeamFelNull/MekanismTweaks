package dev.felnull.mekanismtweaks;

import net.minecraft.util.text.ITextComponent;

import java.util.function.BooleanSupplier;

/**
 * Added to Mekanism's WarningTracker by a mixin. It lets us show a warning of our own in the warning tab of a machine GUI,
 * as WarningTracker.WarningType is an enum that cannot be extended.
 */
public interface IExtraWarnings {

    void mekanismtweaks$addExtra(BooleanSupplier check, ITextComponent message);
}
