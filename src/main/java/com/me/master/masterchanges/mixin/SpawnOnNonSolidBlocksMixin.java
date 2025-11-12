package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(NaturalSpawner.class)
public class SpawnOnNonSolidBlocksMixin {
    @Inject(method = "isSpawnPositionOk", at = @At("HEAD"), cancellable = true)
    private static void allowSpawnOnNonSolid(net.minecraft.world.entity.SpawnPlacements.Type spawnType, LevelReader level, BlockPos pos, net.minecraft.world.entity.EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.SPAWN_ON_NON_SOLID)) {
            Block block = level.getBlockState(pos.below()).getBlock();
            if (block instanceof FenceBlock || block instanceof FenceGateBlock || 
                block instanceof DoorBlock || block instanceof ChainBlock ||
                block instanceof TrapDoorBlock) {
                cir.setReturnValue(true);
            }
        }
    }
}
