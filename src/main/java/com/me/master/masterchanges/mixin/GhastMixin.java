package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(targets = "net.minecraft.world.entity.monster.Ghast$GhastShootFireballGoal")
public class GhastMixin {

    @Shadow @Final private Ghast ghast;
    @Shadow public int chargeTime;

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void onShootFireball(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.GHAST_DOUBLE_FIREBALL)) {
            Level level = this.ghast.level();
            LivingEntity target = this.ghast.getTarget();
            if (target != null && !level.isClientSide) {
                Vec3 viewVector = this.ghast.getViewVector(1.0F);
                double offsetX = this.ghast.getX() + viewVector.x * config.getFloat("ghast_double_fireball", "multiplier", 4.0f);
                double offsetY = this.ghast.getY(config.getFloat("ghast_double_fireball", "value", config.getFloat("ghast_double_fireball", "value1", 0.5f))) + 0.5;
                double offsetZ = this.ghast.getZ() + viewVector.z * config.getFloat("ghast_double_fireball", "multiplier1", 4.0f);
                double d5 = target.getX() - offsetX;
                double d6 = target.getY(config.getFloat("ghast_double_fireball", "value1", 0.5f)) - offsetY;
                double d7 = target.getZ() - offsetZ;
                int extraBalls = level.random.nextInt(config.getInt("ghast_double_fireball", "countMax", 2)) + 1;
                for (int i = 0; i < extraBalls; i++) {
                    double varianceX = level.random.nextGaussian() * config.getFloat("ghast_double_fireball", "multiplier1", 0.4f);
                    double varianceY = level.random.nextGaussian() * config.getFloat("ghast_double_fireball", "multiplier1", 0.3f);
                    double varianceZ = level.random.nextGaussian() * config.getFloat("ghast_double_fireball", "multiplier1", 0.4f);
                    LargeFireball fireball = new LargeFireball(level, this.ghast,
                            d5 + varianceX, d6 + varianceY, d7 + varianceZ, this.ghast.getExplosionPower());
                    fireball.setPos(offsetX + varianceX * config.getFloat("ghast_double_fireball", "multiplier1", config.getFloat("ghast_double_fireball", "multiplier1", config.getFloat("ghast_double_fireball", "multiplier1", 0.5f))), offsetY + varianceY * 0.5, offsetZ + varianceZ * 0.5);
                    level.addFreshEntity(fireball);
                }

                if (!this.ghast.isSilent()) {
                    level.levelEvent(null, config.getInt("ghast_double_fireball", "value1", 1016), this.ghast.blockPosition(), 0);
                }
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
            shift = At.Shift.AFTER))
    private void reduceCooldown(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.GHAST_DOUBLE_FIREBALL)) {
            if (this.chargeTime == -config.getInt("ghast_double_fireball", "duration", 40)) {
                this.chargeTime = -config.getInt("ghast_double_fireball", "duration1", 20);
            }
        }
    }
}
