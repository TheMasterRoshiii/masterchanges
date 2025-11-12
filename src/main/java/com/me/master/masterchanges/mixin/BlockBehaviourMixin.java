package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockBehaviourMixin {
    
    @Inject(method = "isValidSpawn", at = @At("HEAD"), cancellable = true)
    private void onIsValidSpawn(BlockGetter level, BlockPos pos, 
                                 net.minecraft.world.entity.EntityType<?> entityType, 
                                 CallbackInfoReturnable<Boolean> cir) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        DifficultyManager manager = DifficultyManager.getInstance();
        BlockState state = (BlockState) (Object) this;
        
        if (manager.isFeatureEnabled(DifficultyFeature.SPAWN_ON_BUTTONS) && state.getBlock() instanceof ButtonBlock) {
            cir.setReturnValue(true);
            return;
        }
        
        if (manager.isFeatureEnabled(DifficultyFeature.SPAWN_ON_LEAVES) && state.getBlock() instanceof LeavesBlock) {
            cir.setReturnValue(true);
            return;
        }
        
        if (manager.isFeatureEnabled(DifficultyFeature.SPAWN_ON_SLABS) && state.getBlock() instanceof SlabBlock) {
            cir.setReturnValue(true);
        }
    }
}
