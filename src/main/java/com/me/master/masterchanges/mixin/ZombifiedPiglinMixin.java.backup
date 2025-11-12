package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombifiedPiglin.class)
public class ZombifiedPiglinMixin {

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void onCustomServerAiStep(CallbackInfo ci) {
        ZombifiedPiglin zombifiedPiglin = (ZombifiedPiglin) (Object) this;

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PIGLINS_ALWAYS_HOSTILE)) {
            if (zombifiedPiglin.getTarget() == null && zombifiedPiglin.tickCount % 20 == 0) {
                net.minecraft.world.entity.player.Player nearestPlayer = zombifiedPiglin.level().getNearestPlayer(zombifiedPiglin, 16.0);
                if (nearestPlayer != null) {
                    zombifiedPiglin.setTarget(nearestPlayer);
                    zombifiedPiglin.setRemainingPersistentAngerTime(400);
                }
            }
        }
    }
}
