package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(Giant.class)
public abstract class GiantAIMixin extends net.minecraft.world.entity.monster.Monster {

    public GiantAIMixin(EntityType<? extends net.minecraft.world.entity.monster.Monster> type, Level level) {
        super(type, level);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
    private void addGiantAI(EntityType<?> entityType, Level level, CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (level.isClientSide || !DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.GIANT_AI)) return;

        Giant giant = (Giant) (Object) this;

        giant.goalSelector.addGoal(0, new FloatGoal(giant));
        giant.goalSelector.addGoal(1, new MeleeAttackGoal(giant, config.getFloat("giant_ai", "damage", 0.3f), false) {
            @Override
            protected double getAttackReachSqr(LivingEntity target) {
                return config.getFloat("giant_ai", "value", 64.0f);
            }

            @Override
            protected void checkAndPerformAttack(LivingEntity target, double sqDistance) {
                super.checkAndPerformAttack(target, sqDistance);
                double xDiff = giant.getX() - target.getX();
                double zDiff = giant.getZ() - target.getZ();
                target.knockback(config.getFloat("giant_ai", "value1", 2.0f), xDiff, zDiff);
            }
        });
        giant.goalSelector.addGoal(config.getInt("giant_ai", "value1", 4), new RandomStrollGoal(giant, config.getFloat("giant_ai", "value1", 0.6f)));
        giant.goalSelector.addGoal(config.getInt("giant_ai", "value1", 6), new LookAtPlayerGoal(giant, Player.class, config.getFloat("giant_ai", "value1", 20.0f)));
        giant.goalSelector.addGoal(config.getInt("giant_ai", "value1", 7), new RandomLookAroundGoal(giant));

        giant.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(giant, Player.class, true, true));
    }
}
