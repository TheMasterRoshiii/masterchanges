package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Piglin.class)
public class PiglinMixin {

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void onCustomServerAiStep(CallbackInfo ci) {
        Piglin piglin = (Piglin) (Object) this;

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PIGLINS_ALWAYS_HOSTILE)) {
            if (piglin.getTarget() == null && piglin.tickCount % 20 == 0) {
                net.minecraft.world.entity.player.Player nearestPlayer = piglin.level().getNearestPlayer(piglin, 16.0);
                if (nearestPlayer != null) {
                    piglin.setTarget(nearestPlayer);
                }
            }
        }

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PIGLINS_NO_BARTER)) {
            if (!piglin.getOffhandItem().isEmpty()) {
                piglin.getOffhandItem().shrink(1);
            }
        }
    }
}
