package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.MekanismTweaks;
import mekanism.common.Mekanism;
import mekanism.common.tile.TileEntityContainerBlock;
import mekanism.common.tile.component.TileComponentUpgrade;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileComponentUpgrade.class, remap = false)
public class MixinTileComponentUpgrade {

    @Shadow
    private int upgradeSlot;

    @Shadow
    public int upgradeTicks;

    @Shadow
    public int speedMultiplier;

    @Shadow
    public int energyMultiplier;

    @Shadow
    public TileEntityContainerBlock tileEntity;

    /**
     * Install the whole stack in the upgrade slot at once, as many as the maximum allows, instead of one upgrade per UPGRADE_TICKS_REQUIRED.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void installAll(CallbackInfo ci) {
        if (!MekanismTweaks.bulkInstall || tileEntity.getWorldObj().isRemote) return;

        ItemStack stack = tileEntity.inventory[upgradeSlot];
        if (stack == null) return;

        boolean energy = stack.isItemEqual(new ItemStack(Mekanism.EnergyUpgrade));
        boolean speed = !energy && stack.isItemEqual(new ItemStack(Mekanism.SpeedUpgrade));
        if (!energy && !speed) return;

        int count = Math.min(stack.stackSize, (energy ? MekanismTweaks.maxEnergy - energyMultiplier : MekanismTweaks.maxSpeed - speedMultiplier));
        if (count <= 0) return;

        if (energy) energyMultiplier += count;
        else speedMultiplier += count;

        stack.stackSize -= count;
        if (stack.stackSize <= 0) tileEntity.inventory[upgradeSlot] = null;

        upgradeTicks = 0;
        tileEntity.markDirty();
    }

    /**
     * Wrap MaxEnergyUpgradesInstalled. (the first "< 8" in tick)
     */
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 8, ordinal = 0))
    private int maxEnergy(int def) {
        return MekanismTweaks.maxEnergy;
    }

    /**
     * Wrap MaxSpeedUpgradesInstalled. (the second "< 8" in tick)
     */
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 8, ordinal = 1))
    private int maxSpeed(int def) {
        return MekanismTweaks.maxSpeed;
    }
}
