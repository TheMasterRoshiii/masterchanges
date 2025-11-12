package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(LivingEntity.class)
public class WitchTotemMixin {

    @Inject(method = "hurt", at = @At("HEAD"))
    private void witchUseTotem(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        LivingEntity entity = (LivingEntity)(Object)this;
        
        if (!(entity instanceof Witch witch)) return;
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.WITCH_TOTEM_USE)) return;
        if (witch.level().isClientSide) return;

        if (witch.getHealth() - amount <= config.getFloat("witch_totem_use", "value", 4.0f)) {
            ItemStack mainHand = witch.getMainHandItem();
            ItemStack offHand = witch.getOffhandItem();
            
            ItemStack totemStack = null;
            if (mainHand.is(Items.TOTEM_OF_UNDYING)) {
                totemStack = mainHand;
            } else if (offHand.is(Items.TOTEM_OF_UNDYING)) {
                totemStack = offHand;
            }
            
            if (totemStack != null) {
                totemStack.shrink(1);
                
                witch.setHealth(config.getFloat("witch_totem_use", "value1", 20.0f));
                witch.clearFire();
                witch.removeAllEffects();
                witch.addEffect(new MobEffectInstance(MobEffects.REGENERATION, config.getInt("witch_totem_use", "value1", 900), 1));
                witch.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, config.getInt("witch_totem_use", "value1", 100), 1));
                witch.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, config.getInt("witch_totem_use", "value1", 800), 0));
                
                if (witch.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                        witch.getX(), witch.getY() + 1.0, witch.getZ(),
                        config.getInt("witch_totem_use", "value1", 50), config.getFloat("witch_totem_use", "value1", config.getFloat("witch_totem_use", "value1", config.getFloat("witch_totem_use", "value1", 0.5f))), 0.5, 0.5, config.getFloat("witch_totem_use", "value1", 0.1f)
                    );
                }
                
                witch.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
            }
        }
    }
}
