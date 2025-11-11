package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
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
        Witch witch = (Witch)(Object)this;
        Level level = witch.level();
        
        if (level.isClientSide) return;

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.WITCH_TELEPORT_ESCAPE)) {
            if (witch.getHealth() < witch.getMaxHealth() * 0.4f && witch.getRandom().nextFloat() < 0.05f) {
                BlockPos targetPos = masterchanges$findSafeTeleportPosition(level, witch.blockPosition(), 16, 10);
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
            if (witch.tickCount % 60 == 0) {
                List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, witch.getBoundingBox().inflate(8.0));
                for (Mob mob : nearbyMobs) {
                    if (mob != witch && mob.getMobType() != MobType.UNDEAD && mob.isAlive()) {
                        mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
                        mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0));
                    }
                }
            }
        }
    }

    @Unique
    private BlockPos masterchanges$findSafeTeleportPosition(Level level, BlockPos start, int horizontalRadius, int verticalRadius) {
        RandomSource random = level.random;
        
        for (int attempt = 0; attempt < 16; attempt++) {
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
