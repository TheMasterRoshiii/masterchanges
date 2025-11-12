package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(Monster.class)
public class MonsterMixin {
    
    @Inject(method = "checkMonsterSpawnRules", at = @At("HEAD"), cancellable = true)
    private static void onCheckMonsterSpawnRules(EntityType<? extends Monster> entityType, 
                                                  ServerLevelAccessor level, 
                                                  net.minecraft.world.entity.MobSpawnType spawnType, 
                                                  BlockPos pos, 
                                                  RandomSource random, 
                                                  CallbackInfoReturnable<Boolean> cir) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.IGNORE_LIGHT_LEVEL)) {
            cir.setReturnValue(pos.getY() < level.getLevel().getMaxBuildHeight());
        }
    }
}
