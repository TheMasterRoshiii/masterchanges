package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.entity.monster.piglin.PiglinAi.class)
public class PiglinAiMixin {

    @Inject(method = "isWearingGold", at = @At("HEAD"), cancellable = true)
    private static void onIsWearingGold(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PIGLINS_ALWAYS_HOSTILE)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isPlayerHoldingLovedItem", at = @At("HEAD"), cancellable = true)
    private static void onIsPlayerHoldingLovedItem(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PIGLINS_NO_BARTER)) {
            cir.setReturnValue(false);
        }
    }
}
