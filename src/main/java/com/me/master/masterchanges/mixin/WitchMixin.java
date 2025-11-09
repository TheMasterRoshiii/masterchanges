package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public class WitchMixin {

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Witch witch && DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.WITCH_TOTEM_USE)) {
            Level level = witch.level();
            if (!level.isClientSide() && witch.getHealth() - amount <= 0.0f) {
                ItemStack mainHand = witch.getMainHandItem();
                ItemStack offHand = witch.getOffhandItem();
                if (mainHand.is(Items.TOTEM_OF_UNDYING) || offHand.is(Items.TOTEM_OF_UNDYING)) {
                    if (!mainHand.isEmpty() && mainHand.is(Items.TOTEM_OF_UNDYING)) {
                        mainHand.shrink(1);
                    } else {
                        offHand.shrink(1);
                    }
                    witch.setHealth(1.0f);
                    witch.clearFire();
                    witch.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                    witch.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                    witch.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                    level.levelEvent(null, 1038, witch.blockPosition(), 0);
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void onAiStep(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Witch witch) {
            Level level = witch.level();
            if (!level.isClientSide()) {
                if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.WITCH_TELEPORT_ESCAPE)) {
                    if (witch.getHealth() < 15.0f && RandomSource.create().nextFloat() < 0.4f) {
                        BlockPos pos = witch.blockPosition();
                        BlockPos targetPos = findSafeTeleportPosition(level, pos, 32, 8);
                        if (targetPos != null) {
                            witch.teleportTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
                        }
                    }
                }
                if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.WITCH_HEAL_MOBS)) {
                    if (witch.tickCount % 60 == 0) {
                        List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, witch.getBoundingBox().inflate(8.0));
                        for (Mob mob : nearbyMobs) {
                            if (mob != witch && mob.getMobType() != MobType.UNDEAD) {
                                mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
                                mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0));
                            }
                        }
                    }
                }
            }
        }
    }

    private BlockPos findSafeTeleportPosition(Level level, BlockPos start, int horizontalRadius, int verticalRadius) {
        RandomSource random = RandomSource.create();
        double currentY = start.getY();
        for (int attempt = 0; attempt < 20; attempt++) {
            int dx = Mth.floor(random.nextGaussian() * horizontalRadius);
            int dy = Math.max(2, Mth.floor(random.nextGaussian() * verticalRadius + currentY));
            if (dy < 2) dy = 2;
            int dz = Mth.floor(random.nextGaussian() * horizontalRadius);
            BlockPos candidate = new BlockPos(start.getX() + dx, dy, start.getZ() + dz);
            BlockState state = level.getBlockState(candidate);
            FluidState fluid = level.getFluidState(candidate);
            BlockState below = level.getBlockState(candidate.below());
            if (state.isAir() && fluid.isEmpty() && below.isSolid() && candidate.getY() >= 2) {
                return candidate;
            }
        }
        return null;
    }
}
