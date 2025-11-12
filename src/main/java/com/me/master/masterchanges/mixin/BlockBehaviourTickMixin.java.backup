package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourTickMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!level.isClientSide && DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.ACID_RAIN_BREAK_WEAK_BLOCKS)) {
            if (state.is(Blocks.OAK_LEAVES) ||
                    state.is(Blocks.SPRUCE_LEAVES) ||
                    state.is(Blocks.BIRCH_LEAVES) ||
                    state.is(Blocks.OAK_LOG) ||
                    state.is(Blocks.SPRUCE_LOG) ||
                    state.is(Blocks.BIRCH_LOG)) {
                if (level.isRaining() && level.canSeeSky(pos)) {
                    if (random.nextInt(200) == 0) {
                        level.destroyBlock(pos, false);
                    }
                }
            }
        }
    }
}
