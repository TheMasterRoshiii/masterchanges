package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(EnderMan.class)
public class EndermanMixin {

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void cancelTotem(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        EnderMan enderman = (EnderMan) (Object) this;
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.ENDERMAN_CANCEL_TOTEM)) {
            if (source.getEntity() instanceof EnderMan && enderman.getHealth() - amount <= 0.0f) {
                ItemStack mainHand = enderman.getMainHandItem();
                ItemStack offHand = enderman.getOffhandItem();
                if (mainHand.is(Items.TOTEM_OF_UNDYING) || offHand.is(Items.TOTEM_OF_UNDYING)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void alwaysHostile(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        EnderMan enderman = (EnderMan) (Object) this;
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.ENDERMAN_ALWAYS_HOSTILE)) {
            if (enderman.getTarget() == null) {
                Player nearestPlayer = enderman.level().getNearestPlayer(enderman, config.getFloat("enderman_cancel_totem", "value", 16.0f));
                if (nearestPlayer != null) {
                    enderman.setTarget(nearestPlayer);
                    ((Mob) enderman).getNavigation().moveTo(nearestPlayer, 1.0);
                }
            }
        }
    }
}
