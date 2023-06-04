package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Utils;
import mekanism.common.Upgrade;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.config.MekanismConfig;
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
        return upgrade == Upgrade.SPEED || upgrade == Upgrade.ENERGY ? 64 : maxStack;
    }

    /**
     * @author ni8995
     * @reason idk
     */
    @Overwrite
    public List<String> getExpScaledInfo(IUpgradeTile tile) {
        List<String> ret = new ArrayList<>();
        if (canMultiply()) {
            ret.add(LangUtils.localize("gui.upgrades.effect") + ": " + Utils.time(tile) + "x");
        }

        return ret;
    }

    /**
     * @author nin8995
     * @reason show on effect what double multiplied to time taken and energy storage
     */
    @Overwrite
    public List<String> getMultScaledInfo(IUpgradeTile tile) {
        List<String> ret = new ArrayList<>();
        Upgrade upgrade = (Upgrade) (Object) this;
        if (this.canMultiply()) {
            double effect =
                    upgrade == Upgrade.ENERGY
                    ? Utils.capacity(tile)
                    : upgrade == Upgrade.SPEED
                      ? Utils.time(tile)
                      : Math.pow(MekanismConfig.current().general.maxUpgradeMultiplier.val(), (float) tile.getComponent().getUpgrades(upgrade) / (float) getMax());
            ret.add(LangUtils.localize("gui.upgrades.effect") + ": " + Utils.exponential(effect) + "x");
        }

        return ret;
    }
}
