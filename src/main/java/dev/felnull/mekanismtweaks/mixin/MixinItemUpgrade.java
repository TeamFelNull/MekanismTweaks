package dev.felnull.mekanismtweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mekanism.common.Upgrade;
import mekanism.common.item.ItemUpgrade;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.tile.component.TileComponentUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ItemUpgrade.class, remap = false)
public class MixinItemUpgrade {

    /**
     * Allows all upgrades to stack up to 64.
     */
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/item/ItemUpgrade;setMaxStackSize(I)Lnet/minecraft/item/Item;", remap = true))
    private int injected(int maxStackSize) {
        return 64;
    }

    @ModifyExpressionValue(method = "onItemUseFirst", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getTileEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/tileentity/TileEntity;", remap = true))
    private TileEntity resolveBoundingBlock(TileEntity tile) {
        if (tile instanceof TileEntityBoundingBlock) {
            TileEntity mainTile = ((TileEntityBoundingBlock) tile).getMainTile();
            if (mainTile != null)
                return mainTile;
        }
        return tile;
    }

    @ModifyArg(method = "onItemUseFirst", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shrink(I)V", remap = true))
    private int installAllHeldUpgrades(int quantity, @Local ItemStack stack, @Local TileComponentUpgrade component, @Local Upgrade type) {
        int amount = Math.min(stack.getCount(), quantity + type.getMax() - component.getUpgrades(type));
        while (quantity++ < amount) component.addUpgrade(type);
        return amount;
    }
}
