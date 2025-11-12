package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(ZombifiedPiglin.class)
public class ZombifiedPiglinMixin {

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void onCustomServerAiStep(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        ZombifiedPiglin zombifiedPiglin = (ZombifiedPiglin) (Object) this;

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PIGLINS_ALWAYS_HOSTILE)) {
            if (zombifiedPiglin.getTarget() == null && zombifiedPiglin.tickCount % config.getInt("piglins_always_hostile", "tickInterval", 20) == 0) {
                net.minecraft.world.entity.player.Player nearestPlayer = zombifiedPiglin.level().getNearestPlayer(zombifiedPiglin, config.getFloat("piglins_always_hostile", "value", 16.0f));
                if (nearestPlayer != null) {
                    zombifiedPiglin.setTarget(nearestPlayer);
                    zombifiedPiglin.setRemainingPersistentAngerTime(config.getInt("piglins_always_hostile", "duration", 400));
                }
            }
        }
    }
}
