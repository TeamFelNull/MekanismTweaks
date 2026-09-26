package dev.felnull.mekanismtweaks.mixin;

import mekanism.api.Upgrade;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.TileEntityDigitalMiner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;
import java.util.Set;

@Mixin(value = TileEntityMekanism.class, remap = false)
public abstract class MixinTileEntityMekanism {

    /**
     * The Digital Miner accepts Muffling Upgrades too.
     */
    @Inject(method = "getSupportedUpgrade", at = @At("RETURN"), cancellable = true)
    private void mekanismtweaks$miningMuffling(CallbackInfoReturnable<Set<Upgrade>> cir) {
        if ((Object) this instanceof TileEntityDigitalMiner && !cir.getReturnValue().contains(Upgrade.MUFFLING)) {
            Set<Upgrade> supported = EnumSet.noneOf(Upgrade.class);
            supported.addAll(cir.getReturnValue());
            supported.add(Upgrade.MUFFLING);
            cir.setReturnValue(supported);
        }
    }
}
