package dev.felnull.mekanismtweaks.mixin;

import dev.felnull.mekanismtweaks.Temp;
import mekanism.api.Upgrade;
import mekanism.common.tile.machine.TileEntityDigitalMiner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityDigitalMiner.class, remap = false)
public abstract class MixinHomoo {

    @Shadow private int delay;

    @Shadow protected abstract void tryMineBlock();

    @Shadow public abstract void recalculateUpgrades(Upgrade upgrade);


    @Shadow public abstract int getDelay();

    @Inject(method = "onUpdateServer", at = @At(value = "INVOKE", target =  "Lmekanism/common/capabilities/energy/MinerEnergyContainer;extract(Lmekanism/api/math/FloatingLong;Lmekanism/api/Action;Lmekanism/api/AutomationType;)Lmekanism/api/math/FloatingLong;", shift = At.Shift.AFTER))
    public void injected(CallbackInfo ci){
        if(Temp.extractFlag) {
            if(delay < 0) {
                var i = delay - 1;
                for (; i < 0; i++)
                    tryMineBlock();
                Temp.extractFlag = false;
                recalculateUpgrades(Upgrade.SPEED);
                ((DelayAccessor) this).setDelay(getDelay());
            }
        }else {
            Temp.extractFlag = true;
        }
    }
}

