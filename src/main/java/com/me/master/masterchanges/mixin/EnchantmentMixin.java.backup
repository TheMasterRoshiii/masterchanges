package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    private static final Random RANDOM = new Random();
    
    @Inject(method = "getDamageProtection", at = @At("RETURN"), cancellable = true)
    private void onGetDamageProtection(int level, net.minecraft.world.damagesource.DamageSource source, CallbackInfoReturnable<Integer> cir) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.ENCHANTMENT_FAIL_CHANCE)) {
            if (RANDOM.nextFloat() < 0.25f) {
                cir.setReturnValue(0);
            }
        }
    }
    
    @Inject(method = "getDamageBonus", at = @At("RETURN"), cancellable = true)
    private void onGetDamageBonus(int level, net.minecraft.world.entity.MobType mobType, CallbackInfoReturnable<Float> cir) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.ENCHANTMENT_FAIL_CHANCE)) {
            if (RANDOM.nextFloat() < 0.25f) {
                cir.setReturnValue(0.0f);
            }
        }
    }
}
