package dev.felnull.mekanismtweaks.mixin;

import mekanism.client.gui.GuiUpgradeManagement;
import mekanism.common.Upgrade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import javax.annotation.Nullable;

@Mixin(value = GuiUpgradeManagement.class, remap = false)
public class MixinGuiUpgradeManagement {

    @Shadow @Nullable private Upgrade selectedType;

    @ModifyArg(method = "func_146979_b", at = @At(value = "INVOKE", target = "Lmekanism/client/gui/GuiUpgradeManagement;renderText(Ljava/lang/String;IIFZ)V", ordinal = 2), index = 0)
    private String unlimit(String text){
        return selectedType.getMax() == Integer.MAX_VALUE ? text.split("/")[0] : text;
    }
}
