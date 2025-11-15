package com.me.master.masterchanges.mixin;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobSunImmunityMixin {
    
    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void preventSunBurn(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
