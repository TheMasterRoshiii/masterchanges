package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.DoorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class DoorDamageMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void doorDamage(CallbackInfo ci) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.DOOR_INSANE_DAMAGE)) {
            LivingEntity entity = (LivingEntity)(Object)this;
            BlockPos pos = entity.blockPosition();

            if (entity.level().getBlockState(pos).getBlock() instanceof DoorBlock ||
                    entity.level().getBlockState(pos.above()).getBlock() instanceof DoorBlock) {
                entity.hurt(entity.level().damageSources().generic(), 9000000.0F);
            }
        }
    }
}
