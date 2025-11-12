package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(BedBlock.class)
public class BedBlockMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onBedUse(BlockState state, Level level, BlockPos pos, Player player,
                          InteractionHand hand, BlockHitResult hit,
                          CallbackInfoReturnable<InteractionResult> cir) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.BEDS_EXPLODE_NIGHT)) {
            if (!level.isClientSide) {
                long dayTime = level.getDayTime() % config.getInt("beds_explode_night", "duration", 24000);
                boolean isNight = dayTime >= config.getInt("beds_explode_night", "duration1", 12542) && dayTime <= config.getInt("beds_explode_night", "duration1", 23460);

                if (isNight) {
                    level.removeBlock(pos, false);
                    level.explode(null, pos.getX() + config.getFloat("beds_explode_night", "value", config.getFloat("beds_explode_night", "value1", config.getFloat("beds_explode_night", "value1", 0.5f))), pos.getY() + 0.5, pos.getZ() + 0.5,
                            config.getFloat("beds_explode_night", "value1", 5.0f), true, Level.ExplosionInteraction.BLOCK);
                    cir.setReturnValue(InteractionResult.SUCCESS);
                }
            }
        }
    }
}
