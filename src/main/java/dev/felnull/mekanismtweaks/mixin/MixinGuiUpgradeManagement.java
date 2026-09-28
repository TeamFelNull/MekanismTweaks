package dev.felnull.mekanismtweaks.mixin;

import mekanism.client.gui.GuiUpgradeManagement;
import mekanism.common.Upgrade;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import javax.annotation.Nullable;

@Mixin(value = GuiUpgradeManagement.class, remap = false)
public class MixinGuiUpgradeManagement {

    @Shadow @Nullable private Upgrade selectedType;

    @ModifyArg(method = "drawGuiContainerForegroundLayer", at = @At(value = "INVOKE", target = "Lmekanism/client/gui/GuiUpgradeManagement;renderText(Ljava/lang/String;IIFZ)V", ordinal = 2, remap = false), index = 0, remap = true)
    private String unlimit(String text){
        return selectedType != null && selectedType.getMax() == Integer.MAX_VALUE ? text.split("/")[0] : text;
    }

    @ModifyArg(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lmekanism/common/network/PacketRemoveUpgrade$RemoveUpgradeMessage;<init>(Lmekanism/api/Coord4D;I)V", remap = false), index = 1, remap = true)
    private int removeAllUpgradesWhenShiftIsDown(int upgradeType) {
        return GuiScreen.isShiftKeyDown() ? upgradeType + Upgrade.values().length : upgradeType;
    }
}
