package dev.felnull.mekanismtweaks.mixin;

import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.item.ItemUpgrade;
import mekanism.common.tile.TileEntityBoundingBlock;
import mekanism.common.tile.component.TileComponentUpgrade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemUpgrade.class, remap = false)
public class MixinItemUpgrade {

    /**
     * Limit MaxStackSize to 64 to avoid unstable item interaction behavior.
     */
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lmekanism/common/item/ItemUpgrade;setMaxStackSize(I)Lnet/minecraft/item/Item;", remap = true))
    private int injected(int maxStackSize) {
        return Math.min(maxStackSize, 64);
    }

    @Inject(method = "onItemUseFirst", at = @At("HEAD"), cancellable = true)
    private void installAllHeldUpgrades(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand, CallbackInfoReturnable<EnumActionResult> cir) {
        if (!player.isSneaking())
            return;

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBoundingBlock) {
            TileEntity mainTile = ((TileEntityBoundingBlock) tile).getMainTile();
            if (mainTile != null)
                tile = mainTile;
        }

        if (!(tile instanceof IUpgradeTile))
            return;

        ItemStack stack = player.getHeldItem(hand);
        Upgrade type = ((ItemUpgrade) (Object) this).getUpgradeType(stack);
        TileComponentUpgrade component = ((IUpgradeTile) tile).getComponent();
        if (component.supports(type) && !world.isRemote) {
            int amount = Math.min(stack.getCount(), type.getMax() - component.getUpgrades(type));
            for (int i = 0; i < amount; i++)
                component.addUpgrade(type);
            stack.shrink(amount);
        }
        cir.setReturnValue(EnumActionResult.SUCCESS);
    }
}
