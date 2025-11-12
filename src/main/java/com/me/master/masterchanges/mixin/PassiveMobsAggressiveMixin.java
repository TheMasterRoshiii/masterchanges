package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(Animal.class)
public abstract class PassiveMobsAggressiveMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void makeAnimalsAggressive(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        Animal animal = (Animal)(Object)this;

        if (animal instanceof Axolotl) return;
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PASSIVE_MOBS_AGGRESSIVE)) return;

        if (animal instanceof PathfinderMob) {
            PathfinderMob pathfinder = (PathfinderMob) animal;
            pathfinder.targetSelector.addGoal(1, new HurtByTargetGoal(pathfinder));
            pathfinder.targetSelector.addGoal(config.getInt("passive_mobs_aggressive", "damage", 2), new NearestAttackableTargetGoal<>(pathfinder, Player.class, true));
            pathfinder.goalSelector.addGoal(1, new MeleeAttackGoal(pathfinder, config.getFloat("passive_mobs_aggressive", "damage1", 1.2f), false));
        }
    }
}
