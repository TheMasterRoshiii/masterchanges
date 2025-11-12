package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.level.NaturalSpawner$SpawnState")
public class NaturalSpawnerMixin {

    @Inject(method = "canSpawn", at = @At("HEAD"), cancellable = true)
    private void onCanSpawn(CallbackInfoReturnable<Boolean> cir) {
        DifficultyManager manager = DifficultyManager.getInstance();

        if (manager.isFeatureEnabled(DifficultyFeature.SPAWN_ON_BUTTONS) ||
                manager.isFeatureEnabled(DifficultyFeature.SPAWN_ON_LEAVES) ||
                manager.isFeatureEnabled(DifficultyFeature.SPAWN_ON_SLABS)) {
            cir.setReturnValue(true);
        }
    }
}
