package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.config.MixinConfigManager;
import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketMixin {

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void onEntityHit(EntityHitResult result, CallbackInfo ci) {
        FireworkRocketEntity rocket = (FireworkRocketEntity)(Object)this;
        Entity owner = rocket.getOwner();

        if (!(owner instanceof Pillager)) return;
        if (rocket.level().isClientSide) return;
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PILLAGER_ROCKET)) return;

        MixinConfigManager config = MixinConfigManager.getInstance();
        float explosionRadius = config.getFloat("pillager_rocket", "explosionRadius", 2.5f);
        boolean setFire = config.getBoolean("pillager_rocket", "setFire", false);

        rocket.level().explode(
                rocket,
                rocket.getX(),
                rocket.getY(),
                rocket.getZ(),
                explosionRadius,
                setFire,
                Level.ExplosionInteraction.MOB
        );
    }

    @Inject(method = "onHitBlock", at = @At("HEAD"))
    private void onBlockHit(BlockHitResult result, CallbackInfo ci) {
        FireworkRocketEntity rocket = (FireworkRocketEntity)(Object)this;
        Entity owner = rocket.getOwner();

        if (!(owner instanceof Pillager)) return;
        if (rocket.level().isClientSide) return;
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PILLAGER_ROCKET)) return;

        MixinConfigManager config = MixinConfigManager.getInstance();
        float explosionRadius = config.getFloat("pillager_rocket", "explosionRadius", 2.5f);
        boolean setFire = config.getBoolean("pillager_rocket", "setFire", false);

        rocket.level().explode(
                rocket,
                rocket.getX(),
                rocket.getY(),
                rocket.getZ(),
                explosionRadius,
                setFire,
                Level.ExplosionInteraction.MOB
        );
    }
}
