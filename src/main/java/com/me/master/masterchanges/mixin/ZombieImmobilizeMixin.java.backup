package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class ZombieImmobilizeMixin {

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveImmobilizeFlag(CompoundTag tag, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player && player.getPersistentData().getInt("immobilizeTicks") > 0) {
            tag.putInt("immobilizeTicks", player.getPersistentData().getInt("immobilizeTicks"));
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readImmobilizeFlag(CompoundTag tag, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Player player) {
            player.getPersistentData().putInt("immobilizeTicks", tag.getInt("immobilizeTicks"));
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void immobilizeOnZombieHit(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!(entity instanceof Player player)) return;
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.ZOMBIE_IMMOBILIZE)) return;
        if (player.level().isClientSide) return;
        if (player.getLastHurtByMob() instanceof Zombie && player.getLastHurtByMobTimestamp() > player.tickCount - 2) {
            player.getPersistentData().putInt("immobilizeTicks", 60);
        }
        int ticks = player.getPersistentData().getInt("immobilizeTicks");
        if (ticks > 0) {
            player.setDeltaMovement(0, player.getDeltaMovement().y, 0);
            player.getPersistentData().putInt("immobilizeTicks", ticks - 1);
            if (ticks == 1) player.getPersistentData().remove("immobilizeTicks");
        }
    }
}
