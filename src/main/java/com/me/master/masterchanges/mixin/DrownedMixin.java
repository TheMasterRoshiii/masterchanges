package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(LivingEntity.class)
public class DrownedMixin {

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void fastTrident(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof Drowned drowned && DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.DROWNED_FAST_TRIDENT)) {
            if (drowned.tickCount % config.getFloat("drowned_fast_trident", "chance", 20f) == 0 && RandomSource.create().nextFloat() < config.getFloat("drowned_fast_trident", "chance1", 0.6f)) {
                Player nearestPlayer = drowned.level().getNearestPlayer(drowned, config.getFloat("drowned_fast_trident", "value", 16.0f));
                if (nearestPlayer != null && drowned.getMainHandItem().is(Items.TRIDENT)) {
                    drowned.setTarget(nearestPlayer);
                    drowned.performRangedAttack(nearestPlayer, 1.0f);
                }
            }
        }
    }
}
