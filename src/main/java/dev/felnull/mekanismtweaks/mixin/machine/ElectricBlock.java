package dev.felnull.mekanismtweaks.mixin.machine;

import dev.felnull.mekanismtweaks.MekanismTweaks;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import mekanism.common.tile.prefab.TileEntityElectricBlock;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = TileEntityElectricBlock.class, remap = false)
public abstract class ElectricBlock extends TileEntityContainerBlock {

    @Shadow
    public double maxEnergy;

    @Shadow
    public abstract void setEnergy(double energy);

    @Shadow
    public double BASE_MAX_ENERGY;

    @Shadow
    public abstract double getMaxEnergy();

    @Shadow
    public abstract double getEnergy();

    public ElectricBlock(String name) {
        super(name);
    }

    /**
     * MaxEnergy can now also rely on SpeedUpgrade.
     */
    @Override
    public void recalculateUpgradables(Upgrade upgradeType) {
        super.recalculateUpgradables(upgradeType);
        if (MekanismTweaks.avoidExcessiveEnergyBuffer && upgradeType == Upgrade.SPEED) {
            maxEnergy = MekanismUtils.getMaxEnergy((IUpgradeTile) this, BASE_MAX_ENERGY);
            setEnergy(Math.min(getMaxEnergy(), getEnergy()));
        }
    }
}
