package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(AbstractArrow.class)
public class StrayFreezeMixin {

    @Inject(method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", at = @At("TAIL"))
    private void applyFreezeOnStrayArrowHit(EntityHitResult result, CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        AbstractArrow arrow = (AbstractArrow) (Object) this;
        Entity hitEntity = result.getEntity();

        if (hitEntity instanceof Player player &&
                arrow.getOwner() instanceof Stray stray &&
                DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.STRAY_SNOW_FREEZE)) {

            player.setTicksFrozen(config.getInt("stray_snow_freeze", "value", 200));
        }
    }
}
