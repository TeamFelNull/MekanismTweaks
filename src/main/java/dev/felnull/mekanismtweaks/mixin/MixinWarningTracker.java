package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.IExtraWarnings;
import mekanism.common.inventory.warning.WarningTracker;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

@Mixin(WarningTracker.class)
public abstract class MixinWarningTracker implements IExtraWarnings {

    @Unique
    private final List<BooleanSupplier> mekanismtweaks$checks = new ArrayList<>();
    @Unique
    private final List<Component> mekanismtweaks$messages = new ArrayList<>();

    @Override
    public void mekanismtweaks$addExtra(BooleanSupplier check, Component message) {
        mekanismtweaks$checks.add(check);
        mekanismtweaks$messages.add(message);
    }

    @Inject(method = "clearTrackedWarnings", at = @At("HEAD"))
    private void mekanismtweaks$clear(CallbackInfo ci) {
        mekanismtweaks$checks.clear();
        mekanismtweaks$messages.clear();
    }

    /**
     * The warning tab is shown if there is any warning.
     */
    @Inject(method = "hasWarning", at = @At("HEAD"), cancellable = true)
    private void mekanismtweaks$hasWarning(CallbackInfoReturnable<Boolean> cir) {
        for (BooleanSupplier check : mekanismtweaks$checks) {
            if (check.getAsBoolean()) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    /**
     * The warnings that are listed in the tooltip of the warning tab.
     */
    @Inject(method = "getWarnings", at = @At("RETURN"))
    private void mekanismtweaks$getWarnings(CallbackInfoReturnable<List<Component>> cir) {
        for (int i = 0; i < mekanismtweaks$checks.size(); i++) {
            if (mekanismtweaks$checks.get(i).getAsBoolean()) {
                cir.getReturnValue().add(mekanismtweaks$messages.get(i));
            }
        }
    }
}
