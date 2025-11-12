package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ShieldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(LivingEntity.class)
public class ShieldMixin {
    private static final Random RANDOM = new Random();
    
    @Inject(method = "isDamageSourceBlocked", at = @At("HEAD"), cancellable = true)
    private void onIsBlocked(net.minecraft.world.damagesource.DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        LivingEntity entity = (LivingEntity) (Object) this;
        
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.SHIELD_FAIL_CHANCE)) {
            if (entity.isBlocking() && entity.getUseItem().getItem() instanceof ShieldItem) {
                if (RANDOM.nextFloat() < config.getFloat("shield_fail_chance", "chance", 0.3f)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
