package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(Ravager.class)
public class RavagerMixin {

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void breakBlocksIfTargetHigh(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        Ravager ravager = (Ravager) (Object) this;
        Level level = ((net.minecraft.world.entity.Entity) (Object) ravager).level();

        if (!level.isClientSide &&
                DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.RAVAGER_CHARGE_BLOCKS) &&
                ravager.getTarget() != null) {

            double targetY = ravager.getTarget().getY();
            double selfY = ravager.getY();

            if (targetY > selfY + config.getFloat("ravager_charge_blocks", "value", 3.0f)) {
                BlockPos pos = ravager.blockPosition();
                BlockPos frontPos1 = pos.relative(ravager.getDirection());
                BlockPos frontPos2 = frontPos1.above();

                if (!level.getBlockState(frontPos1).isAir() && level.getBlockState(frontPos1).getBlock() != Blocks.BEDROCK) {
                    level.destroyBlock(frontPos1, true);
                }
                if (!level.getBlockState(frontPos2).isAir() && level.getBlockState(frontPos2).getBlock() != Blocks.BEDROCK) {
                    level.destroyBlock(frontPos2, true);
                }
            }
        }
    }
}
