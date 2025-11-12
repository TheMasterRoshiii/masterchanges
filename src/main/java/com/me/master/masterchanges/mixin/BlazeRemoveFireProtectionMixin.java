package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(SmallFireball.class)
public class BlazeRemoveFireProtectionMixin {

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void removeFireProtection(EntityHitResult result, CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.BLAZE_REMOVE_FIRE_PROTECTION)) return;
        
        if (result.getEntity() instanceof LivingEntity target) {
            target.removeEffect(MobEffects.FIRE_RESISTANCE);
        }
    }
}
