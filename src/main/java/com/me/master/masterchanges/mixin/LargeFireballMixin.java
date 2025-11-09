package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LargeFireball.class)
public abstract class LargeFireballMixin {

    @Inject(method = "onHit", at = @At("HEAD"))
    private void onHit(HitResult result, CallbackInfo ci) {
        LargeFireball fireball = (LargeFireball) (Object) this;
        Level level = fireball.level();

        if (!level.isClientSide && DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.GHAST_EXPLOSION_MULTIPLIER)) {
            float multiplier = DifficultyManager.getInstance().getGhastExplosionMultiplier();
            float basePower = 1.0f;

            Entity owner = fireball.getOwner();
            level.explode(owner, fireball.getX(), fireball.getY(), fireball.getZ(),
                    basePower * multiplier, Level.ExplosionInteraction.MOB);
            fireball.discard();
        }
    }
}
