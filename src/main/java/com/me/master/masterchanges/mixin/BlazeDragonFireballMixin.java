package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(targets = "net.minecraft.world.entity.monster.Blaze$BlazeAttackGoal")
public class BlazeDragonFireballMixin {

    @Shadow @Final private Blaze blaze;

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
            cancellable = true)
    private void shootDragonFireball(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.BLAZE_BLUE_FIRE)) return;

        Level level = this.blaze.level();
        LivingEntity target = this.blaze.getTarget();
        
        if (target != null && !level.isClientSide) {
            double d0 = target.getX() - this.blaze.getX();
            double d1 = target.getY(config.getFloat("blaze_blue_fire", "value", config.getFloat("blaze_blue_fire", "value1", 0.5f))) - this.blaze.getY(0.5);
            double d2 = target.getZ() - this.blaze.getZ();
            
            DragonFireball dragonFireball = new DragonFireball(level, this.blaze, d0, d1, d2);
            dragonFireball.setPos(dragonFireball.getX(), this.blaze.getY(config.getFloat("blaze_blue_fire", "value1", config.getFloat("blaze_blue_fire", "value1", 0.5f))) + 0.5, dragonFireball.getZ());
            level.addFreshEntity(dragonFireball);
            
            ci.cancel();
        }
    }
}
