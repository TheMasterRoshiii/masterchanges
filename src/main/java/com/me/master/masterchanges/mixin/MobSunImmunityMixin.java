package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.config.MixinConfigManager;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobSunImmunityMixin {

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void preventSunBurn(CallbackInfoReturnable<Boolean> cir) {
        if (MixinConfigManager.getInstance().isMixinEnabled("mob_sun_immunity")) {
            cir.setReturnValue(false);
        }
    }
}
