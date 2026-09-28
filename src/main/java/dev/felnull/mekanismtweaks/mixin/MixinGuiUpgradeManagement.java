package dev.felnull.mekanismtweaks.mixin;

import mekanism.client.gui.GuiUpgradeManagement;
import mekanism.common.Upgrade;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import javax.annotation.Nullable;

@Mixin(value = GuiUpgradeManagement.class, remap = false)
public class MixinGuiUpgradeManagement {

    @Shadow @Nullable private Upgrade selectedType;

    @Dynamic
    @ModifyArg(method = {"drawGuiContainerForegroundLayer", "func_146979_b"}, at = @At(value = "INVOKE", target = "Lmekanism/client/gui/GuiUpgradeManagement;renderText(Ljava/lang/String;IIFZ)V", ordinal = 2), index = 0)
    private String unlimit(String text){
        return selectedType != null && selectedType.getMax() == Integer.MAX_VALUE ? text.split("/")[0] : text;
    }

    @Dynamic
    @ModifyArg(method = {"actionPerformed", "func_146284_a"}, at = @At(value = "INVOKE", target = "Lmekanism/common/network/PacketRemoveUpgrade$RemoveUpgradeMessage;<init>(Lmekanism/api/Coord4D;I)V"), index = 1)
    private int removeAllUpgradesWhenShiftIsDown(int upgradeType) {
        return GuiScreen.isShiftKeyDown() ? upgradeType + Upgrade.values().length : upgradeType;
    }
}
