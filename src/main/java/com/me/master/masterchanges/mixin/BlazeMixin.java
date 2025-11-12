package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(targets = "net.minecraft.world.entity.monster.Blaze$BlazeAttackGoal")
public class BlazeMixin {

    @Shadow @Final private Blaze blaze;

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void onShootFireball(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.BLAZE_TRIPLE_SHOT)) {
            Level level = this.blaze.level();
            LivingEntity target = this.blaze.getTarget();
            if (target != null && !level.isClientSide) {
                double d0 = target.getX() - this.blaze.getX();
                double d1 = target.getY(config.getFloat("blaze_triple_shot", "value", config.getFloat("blaze_triple_shot", "value1", 0.5f))) - this.blaze.getY(0.5);
                double d2 = target.getZ() - this.blaze.getZ();
                for (int i = 0; i < config.getInt("blaze_triple_shot", "count1", 2); i++) {
                    double varianceX = level.random.nextGaussian() * config.getFloat("blaze_triple_shot", "multiplier", 0.5f);
                    double varianceY = level.random.nextGaussian() * config.getFloat("blaze_triple_shot", "multiplier1", 0.2f);
                    double varianceZ = level.random.nextGaussian() * config.getFloat("blaze_triple_shot", "multiplier1", 0.5f);
                    SmallFireball fireball = new SmallFireball(level, this.blaze,
                            d0 + varianceX, d1 + varianceY, d2 + varianceZ);
                    fireball.setPos(fireball.getX(), this.blaze.getY(config.getFloat("blaze_triple_shot", "value1", config.getFloat("blaze_triple_shot", "value1", 0.5f))) + 0.5, fireball.getZ());
                    level.addFreshEntity(fireball);
                }
            }
        }
    }
}
