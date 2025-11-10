package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class PassiveMobsAggressiveMixin {
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void makePassiveMobsAggressive(CallbackInfo ci) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PASSIVE_MOBS_AGGRESSIVE)) {
            Mob mob = (Mob)(Object)this;
            if (mob instanceof Animal && !(mob instanceof Axolotl)) {
                if (mob instanceof PathfinderMob pathfinder) {
                    pathfinder.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(pathfinder, Player.class, true));
                }
            }
        }
    }
}
