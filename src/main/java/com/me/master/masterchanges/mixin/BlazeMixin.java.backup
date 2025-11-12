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

@Mixin(targets = "net.minecraft.world.entity.monster.Blaze$BlazeAttackGoal")
public class BlazeMixin {

    @Shadow @Final private Blaze blaze;

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void onShootFireball(CallbackInfo ci) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.BLAZE_TRIPLE_SHOT)) {
            Level level = this.blaze.level();
            LivingEntity target = this.blaze.getTarget();
            if (target != null && !level.isClientSide) {
                double d0 = target.getX() - this.blaze.getX();
                double d1 = target.getY(0.5) - this.blaze.getY(0.5);
                double d2 = target.getZ() - this.blaze.getZ();
                for (int i = 0; i < 2; i++) {
                    double varianceX = level.random.nextGaussian() * 0.5;
                    double varianceY = level.random.nextGaussian() * 0.2;
                    double varianceZ = level.random.nextGaussian() * 0.5;
                    SmallFireball fireball = new SmallFireball(level, this.blaze,
                            d0 + varianceX, d1 + varianceY, d2 + varianceZ);
                    fireball.setPos(fireball.getX(), this.blaze.getY(0.5) + 0.5, fireball.getZ());
                    level.addFreshEntity(fireball);
                }
            }
        }
    }
}
