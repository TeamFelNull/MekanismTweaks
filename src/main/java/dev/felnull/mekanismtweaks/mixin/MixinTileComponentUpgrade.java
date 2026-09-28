package dev.felnull.mekanismtweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import mekanism.common.Upgrade;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = TileComponentUpgrade.class, remap = false)
public abstract class MixinTileComponentUpgrade {

    @Shadow
    private int upgradeSlot;

    @Shadow
    public TileEntityContainerBlock tileEntity;

    @Shadow
    public abstract int getUpgrades(Upgrade upgrade);

    @Shadow
    public abstract void addUpgrade(Upgrade upgrade);

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shrink(I)V", remap = true))
    private int installAllUpgradesInSlot(int quantity, @Local Upgrade type) {
        int amount = Math.min(tileEntity.inventory.get(upgradeSlot).getCount(), quantity + type.getMax() - getUpgrades(type));
        while (quantity++ < amount) addUpgrade(type);
        return amount;
    }
}
