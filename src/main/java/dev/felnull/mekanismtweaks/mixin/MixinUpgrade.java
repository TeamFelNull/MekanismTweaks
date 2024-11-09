package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Config;
import dev.felnull.mekanismtweaks.Utils;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.util.LangUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = Upgrade.class, remap = false)
public abstract class MixinUpgrade {

    @Shadow
    public abstract boolean canMultiply();

    @Shadow
    private int maxStack;

    /**
     * @author nin8995
     * @reason wrap max stack size
     */
    @Overwrite
    public int getMax() {
        Upgrade upgrade = (Upgrade) (Object) this;
        return (upgrade == Upgrade.SPEED ? Config.maxSpeed : upgrade == Upgrade.ENERGY ? Config.maxEnergy : maxStack);
    }

    /**
     * @author nin8995
     * @reason show effect double
     */
    @Overwrite
    public List<String> getMultScaledInfo(IUpgradeTile tile) {
        List<String> ret = new ArrayList<>();
        Upgrade upgrade = (Upgrade) (Object) this;
        if (this.canMultiply()) {
            double effect = Utils.effect(Utils.frac(tile, upgrade));
            ret.add(LangUtils.localize("gui.upgrades.effect") + ": " + Utils.exponential(effect) + "x");
        }

        return ret;
    }
}