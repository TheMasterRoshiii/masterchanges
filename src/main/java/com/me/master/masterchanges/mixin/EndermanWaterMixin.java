package com.me.master.masterchanges.mixin;

import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
public class EndermanWaterMixin {
    
    @Inject(method = "isSensitiveToWater", at = @At("HEAD"), cancellable = true)
    private void noWaterSensitivity(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
