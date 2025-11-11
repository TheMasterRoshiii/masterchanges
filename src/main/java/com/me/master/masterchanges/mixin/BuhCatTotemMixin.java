package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.item.BuhCatTotemItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class BuhCatTotemMixin {

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void checkBuhCatTotem(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;

        ItemStack mainHand = entity.getMainHandItem();
        ItemStack offHand = entity.getOffhandItem();

        if (mainHand.getItem() instanceof BuhCatTotemItem) {
            if (BuhCatTotemItem.tryActivateTotem(entity, mainHand)) {
                entity.level().broadcastEntityEvent(entity, (byte)35);
                cir.setReturnValue(true);
            }
        } else if (offHand.getItem() instanceof BuhCatTotemItem) {
            if (BuhCatTotemItem.tryActivateTotem(entity, offHand)) {
                entity.level().broadcastEntityEvent(entity, (byte)35);
                cir.setReturnValue(true);
            }
        }
    }
}
