package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeverBlock.class)
public class LeverInteractDamageMixin {

    @Inject(method = "use", at = @At("HEAD"))
    private void damageOnUse(net.minecraft.world.level.block.state.BlockState state,
                            net.minecraft.world.level.Level level,
                            net.minecraft.core.BlockPos pos,
                            Player player,
                            InteractionHand hand,
                            BlockHitResult hit,
                            CallbackInfoReturnable<InteractionResult> cir) {
        if (!level.isClientSide && DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.LEVER_DAMAGE_ON_USE)) {
            player.hurt(level.damageSources().magic(), 9000000.0F);
        }
    }
}
