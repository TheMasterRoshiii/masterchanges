package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    private static final Random RANDOM = new Random();

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void onTotemCheck(net.minecraft.world.damagesource.DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.TOTEM_PROBABILITY)) {
            LivingEntity entity = (LivingEntity) (Object) this;

            ItemStack totemStack = null;
            for (net.minecraft.world.InteractionHand hand : net.minecraft.world.InteractionHand.values()) {
                ItemStack stack = entity.getItemInHand(hand);
                if (stack.is(Items.TOTEM_OF_UNDYING)) {
                    totemStack = stack;
                    break;
                }
            }

            if (totemStack != null) {
                if (RANDOM.nextFloat() < 0.50f) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "canBreatheUnderwater", at = @At("HEAD"), cancellable = true)
    private void onCanBreatheUnderwater(CallbackInfoReturnable<Boolean> cir) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.NO_INHALATION)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void onAiStep(CallbackInfo ci) {
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.TRIPLE_DROWNING_SPEED)) {
            return;
        }

        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.isEyeInFluid(net.minecraft.tags.FluidTags.WATER)) {
            int air = entity.getAirSupply();
            if (air > 0) {
                entity.setAirSupply(Math.max(0, air - 2));
            }
        }
    }
}
