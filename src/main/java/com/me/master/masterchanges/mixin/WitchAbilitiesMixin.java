package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import com.me.master.masterchanges.config.MixinConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Witch.class)
public class WitchAbilitiesMixin {

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void witchAbilities(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();
        Witch witch = (Witch)(Object)this;
        Level level = witch.level();

        if (level.isClientSide) return;

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.WITCH_TELEPORT_ESCAPE)) {
            float healthThreshold = config.getFloat("witch_teleport_escape", "healthThreshold", 0.4f);
            float teleportChance = config.getFloat("witch_teleport_escape", "teleportChance", 0.05f);
            int teleportRange = config.getInt("witch_teleport_escape", "teleportRange", 16);

            if (witch.getHealth() < witch.getMaxHealth() * healthThreshold && witch.getRandom().nextFloat() < teleportChance) {
                BlockPos targetPos = masterchanges$findSafeTeleportPosition(level, witch.blockPosition(), teleportRange, 10, config);
                if (targetPos != null) {
                    witch.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);

                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                                net.minecraft.core.particles.ParticleTypes.PORTAL,
                                witch.getX(), witch.getY() + 1.0, witch.getZ(),
                                30, 0.5, 0.5, 0.5, 0.1
                        );
                    }
                }
            }
        }

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.WITCH_HEAL_MOBS)) {
            int healInterval = config.getInt("witch_heal_mobs", "healInterval", 60);
            double healRange = config.getDouble("witch_heal_mobs", "healRange", 8.0);
            int regenDuration = config.getInt("witch_heal_mobs", "regenDuration", 100);
            int strengthDuration = config.getInt("witch_heal_mobs", "strengthDuration", 200);

            if (witch.tickCount % healInterval == 0) {
                List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, witch.getBoundingBox().inflate(healRange));
                for (Mob mob : nearbyMobs) {
                    if (mob != witch && mob.getMobType() != MobType.UNDEAD && mob.isAlive()) {
                        mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenDuration, 0));
                        mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, strengthDuration, 0));
                    }
                }
            }
        }
    }

    @Unique
    private BlockPos masterchanges$findSafeTeleportPosition(Level level, BlockPos start, int horizontalRadius, int verticalRadius, MixinConfigManager config) {
        RandomSource random = level.random;
        int maxAttempts = config.getInt("witch_teleport_escape", "maxAttempts", 16);

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int dx = random.nextInt(horizontalRadius * 2) - horizontalRadius;
            int dz = random.nextInt(horizontalRadius * 2) - horizontalRadius;
            int dy = random.nextInt(verticalRadius * 2) - verticalRadius;

            BlockPos candidate = new BlockPos(start.getX() + dx, start.getY() + dy, start.getZ() + dz);

            if (candidate.getY() < level.getMinBuildHeight() + 1 || candidate.getY() > level.getMaxBuildHeight() - 2) {
                continue;
            }

            BlockState stateAt = level.getBlockState(candidate);
            BlockState stateAbove = level.getBlockState(candidate.above());
            BlockState stateBelow = level.getBlockState(candidate.below());

            if (!stateAt.isAir() || !stateAbove.isAir()) {
                continue;
            }

            if (stateBelow.isSolid() && level.getFluidState(candidate).isEmpty()) {
                return candidate;
            }
        }

        return null;
    }
}
