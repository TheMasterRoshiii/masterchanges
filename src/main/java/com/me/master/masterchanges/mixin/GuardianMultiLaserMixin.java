package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(Guardian.class)
public abstract class GuardianMultiLaserMixin {

    @Unique
    private final List<Guardian> masterchanges$fakeGuardians = new ArrayList<>();

    @Unique
    private int masterchanges$attackCooldown = 0;

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void multiTargetLaser(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        Guardian guardian = (Guardian) (Object) this;
        Level level = guardian.level();

        if (level.isClientSide || !DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.GUARDIAN_MULTI_TARGET)) return;

        if (!guardian.hasActiveAttackTarget()) {
            masterchanges$cleanupFakeGuardians(level);
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        if (masterchanges$attackCooldown > 0) {
            masterchanges$attackCooldown--;
            return;
        }

        List<Player> nearbyPlayers = serverLevel.getEntitiesOfClass(
                Player.class,
                guardian.getBoundingBox().inflate(config.getDouble("guardian_multi_target", "range", 16.0)),
                player -> player.isAlive() && guardian.distanceToSqr(player) > config.getFloat("guardian_multi_target", "damage", 9.0f) && player != guardian.getActiveAttackTarget()
        );

        if (nearbyPlayers.isEmpty()) {
            masterchanges$cleanupFakeGuardians(level);
            return;
        }

        nearbyPlayers.sort(Comparator.comparingDouble(guardian::distanceToSqr));
        List<Player> targets = nearbyPlayers.subList(0, Math.min(config.getInt("guardian_multi_target", "value", 2), nearbyPlayers.size()));

        masterchanges$cleanupFakeGuardians(level);

        for (Player player : targets) {
            Guardian fakeGuardian = EntityType.GUARDIAN.create(serverLevel);
            if (fakeGuardian == null) continue;

            fakeGuardian.setPos(guardian.getX(), guardian.getY(), guardian.getZ());
            fakeGuardian.setInvisible(true);
            fakeGuardian.setInvulnerable(true);
            fakeGuardian.setSilent(true);
            fakeGuardian.setNoAi(true);

            masterchanges$setAttackTarget(fakeGuardian, player.getId());

            serverLevel.addFreshEntity(fakeGuardian);
            masterchanges$fakeGuardians.add(fakeGuardian);
        }

        masterchanges$attackCooldown = config.getInt("guardian_multi_target", "damage1", 80);

        float damage = config.getFloat("guardian_multi_target", "damage1", 8.0f);
        if (level.getDifficulty() == net.minecraft.world.Difficulty.HARD) damage += config.getFloat("guardian_multi_target", "damage1", 2.0f);
        if (guardian instanceof net.minecraft.world.entity.monster.ElderGuardian) damage += config.getFloat("guardian_multi_target", "damage1", 2.0f);

        for (Player player : targets) {
            player.hurt(guardian.damageSources().indirectMagic(guardian, guardian), damage);
        }
    }

    @Unique
    private void masterchanges$setAttackTarget(Guardian guardian, int targetId) {
        try {
            Field dataIdField = Guardian.class.getDeclaredField("DATA_ID_ATTACK_TARGET");
            dataIdField.setAccessible(true);
            EntityDataAccessor<Integer> dataId = (EntityDataAccessor<Integer>) dataIdField.get(null);

            Field entityDataField = net.minecraft.world.entity.Entity.class.getDeclaredField("entityData");
            entityDataField.setAccessible(true);
            SynchedEntityData entityData = (SynchedEntityData) entityDataField.get(guardian);

            entityData.set(dataId, targetId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Unique
    private void masterchanges$cleanupFakeGuardians(Level level) {
        if (level.isClientSide) return;
        for (Guardian fake : masterchanges$fakeGuardians) {
            if (fake != null && fake.isAlive()) {
                fake.discard();
            }
        }
        masterchanges$fakeGuardians.clear();
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void cleanupExpiredFakes(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        Guardian guardian = (Guardian) (Object) this;
        if (guardian.level().isClientSide) return;

        masterchanges$fakeGuardians.removeIf(fake -> fake == null || !fake.isAlive() || fake.tickCount > config.getInt("guardian_multi_target", "value1", 85));
    }
}
