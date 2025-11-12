package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(targets = "net.minecraft.world.entity.monster.Shulker$ShulkerAttackGoal")
public class ShulkerMixin {
    
    @Inject(method = "tick", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private void onShootBullet(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.SHULKER_MULTI_SHOT)) {
            try {
                java.lang.reflect.Field shulkerField = this.getClass().getDeclaredField("this$0");
                shulkerField.setAccessible(true);
                Shulker shulker = (Shulker) shulkerField.get(this);
                Level level = shulker.level();
                LivingEntity target = shulker.getTarget();
                
                if (target != null && !level.isClientSide) {
                    for (int i = 0; i < config.getInt("shulker_multi_shot", "count1", 3); i++) {
                        ShulkerBullet bullet = new ShulkerBullet(level, shulker, target, shulker.getAttachFace().getAxis());
                        bullet.setPos(shulker.getX(), shulker.getY() + config.getFloat("shulker_multi_shot", "value", 0.5f), shulker.getZ());
                        level.addFreshEntity(bullet);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
