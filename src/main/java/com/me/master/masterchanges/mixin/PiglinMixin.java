package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.monster.piglin.Piglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(Piglin.class)
public class PiglinMixin {

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void onCustomServerAiStep(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        Piglin piglin = (Piglin) (Object) this;

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PIGLINS_ALWAYS_HOSTILE)) {
            if (piglin.getTarget() == null && piglin.tickCount % config.getInt("piglins_always_hostile", "tickInterval", 20) == 0) {
                net.minecraft.world.entity.player.Player nearestPlayer = piglin.level().getNearestPlayer(piglin, config.getFloat("piglins_always_hostile", "value", 16.0f));
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
