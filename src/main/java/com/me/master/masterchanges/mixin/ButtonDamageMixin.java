package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(ButtonBlock.class)
public class ButtonDamageMixin {
    @Inject(method = "entityInside", at = @At("HEAD"))
    private void buttonDamage(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.BUTTON_INSANE_DAMAGE)) {
            if (entity instanceof LivingEntity) {
                entity.hurt(level.damageSources().generic(), config.getFloat("button_insane_damage", "damage", 9000000.0f));
            }
        }
    }
}
