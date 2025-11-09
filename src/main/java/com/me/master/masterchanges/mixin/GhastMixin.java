package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;  // Import ASM para Opcodes (incluido en Mixin/Forge)
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.entity.monster.Ghast$GhastShootFireballGoal")
public class GhastMixin {

    @Shadow @Final private Ghast ghast;

    @Shadow public int chargeTime;

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void onShootFireball(CallbackInfo ci) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.GHAST_DOUBLE_FIREBALL)) {
            Level level = this.ghast.level();
            LivingEntity target = this.ghast.getTarget();
            if (target != null && !level.isClientSide) {
                Vec3 viewVector = this.ghast.getViewVector(1.0F);
                double offsetX = this.ghast.getX() + viewVector.x * 4.0;
                double offsetY = this.ghast.getY(0.5) + 0.5;
                double offsetZ = this.ghast.getZ() + viewVector.z * 4.0;
                double d5 = target.getX() - offsetX;
                double d6 = target.getY(0.5) - offsetY;
                double d7 = target.getZ() - offsetZ;
                int extraBalls = level.random.nextInt(2) + 1;
                for (int i = 0; i < extraBalls; i++) {
                    double varianceX = level.random.nextGaussian() * 0.4;
                    double varianceY = level.random.nextGaussian() * 0.3;
                    double varianceZ = level.random.nextGaussian() * 0.4;
                    LargeFireball fireball = new LargeFireball(level, this.ghast,
                            d5 + varianceX, d6 + varianceY, d7 + varianceZ, this.ghast.getExplosionPower());
                    fireball.setPos(offsetX + varianceX * 0.5, offsetY + varianceY * 0.5, offsetZ + varianceZ * 0.5);
                    level.addFreshEntity(fireball);
                }

                if (!this.ghast.isSilent()) {
                    level.levelEvent(null, 1016, this.ghast.blockPosition(), 0);
                }
            }
        }
    }


    @ModifyArg(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/monster/Ghast$GhastShootFireballGoal;chargeTime:I", opcode = Opcodes.PUTFIELD), index = 0)
    private int reduceCooldown(int original) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.GHAST_DOUBLE_FIREBALL) && original == -40) {
            return -20;
        }
        return original;
    }
}
