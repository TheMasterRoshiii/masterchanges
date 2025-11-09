package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class VillagerStressMixin {

    private boolean wasOutOfStock = false;

    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    private void checkSpaceStress(CallbackInfo ci) {
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.VILLAGER_STRESS_SMALL_SPACE)) return;

        Villager villager = (Villager) (Object) this;
        Level level = villager.level();
        if (level.isClientSide) return;

        BlockPos pos = villager.blockPosition();
        int airBlocks = 0;
        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y <= 4; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos check = pos.offset(x, y, z);
                    BlockState state = level.getBlockState(check);
                    if (state.isAir()) airBlocks++;
                }
            }
        }

        int threshold = 15;

        if (airBlocks < threshold) {
            if (!wasOutOfStock) {
                villager.getOffers().clear();
                wasOutOfStock = true;
            }
        } else {
            if (wasOutOfStock) {
                villager.restock();
                wasOutOfStock = false;
            }
        }
    }
}
