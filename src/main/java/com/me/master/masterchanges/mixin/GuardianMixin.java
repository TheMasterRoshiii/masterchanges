package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.List;

@Mixin(Guardian.class)
public class GuardianMixin {

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void continuousMaxLaser(CallbackInfo ci) {
        Guardian guardian = (Guardian) (Object) this;
        Level level = guardian.level();
        if (level.isClientSide || !DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.GUARDIAN_MULTI_TARGET)) return;
        if (guardian.tickCount % 5 != 0) return;
        ServerLevel serverLevel = (ServerLevel) level;
        List<Player> nearbyPlayers = serverLevel.getEntitiesOfClass(Player.class, guardian.getBoundingBox().inflate(16.0), player -> player.isAlive() && guardian.distanceToSqr(player) > 9.0);
        if (nearbyPlayers.isEmpty()) return;
        nearbyPlayers.sort(Comparator.comparingDouble(guardian::distanceToSqr));
        nearbyPlayers = nearbyPlayers.subList(0, Math.min(3, nearbyPlayers.size()));
        Player primary = nearbyPlayers.get(0);
        try {
            java.lang.reflect.Field entityDataField = net.minecraft.world.entity.Entity.class.getDeclaredField("entityData");
            entityDataField.setAccessible(true);
            net.minecraft.network.syncher.SynchedEntityData entityData = (net.minecraft.network.syncher.SynchedEntityData) entityDataField.get(guardian);
            java.lang.reflect.Field dataIdField = Guardian.class.getDeclaredField("DATA_ID_ATTACK_TARGET");
            dataIdField.setAccessible(true);
            net.minecraft.network.syncher.EntityDataAccessor<Integer> dataId = (net.minecraft.network.syncher.EntityDataAccessor<Integer>) dataIdField.get(null);
            entityData.set(dataId, primary.getId());
        } catch (Exception ignored) {}
        float damage = 8.0f;
        if (level.getDifficulty() == net.minecraft.world.Difficulty.HARD) damage += 2.0f;
        if (guardian instanceof net.minecraft.world.entity.monster.ElderGuardian) damage += 2.0f;
        for (Player player : nearbyPlayers) {
            player.hurt(guardian.damageSources().indirectMagic(guardian, guardian), damage);
            if (guardian.distanceToSqr(player) < 81.0) {
                player.hurt(guardian.damageSources().mobAttack(guardian), damage);
            }
            Vec3 from = guardian.getEyePosition();
            Vec3 to = player.getEyePosition();
            double dist = from.distanceTo(to);
            double step = 1.8 + serverLevel.random.nextDouble() * 1.7;
            double t = 0.0;
            while (t < dist) {
                t += step;
                Vec3 pos = from.add(to.subtract(from).scale(t / dist));
                serverLevel.sendParticles(ParticleTypes.BUBBLE, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
        guardian.getLookControl().setLookAt(primary, 90.0F, 90.0F);
    }
}
