package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(Vex.class)
public class VexMixin {

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
    private void activateOnSummon(EntityType<? extends Vex> type, Level level, CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        Vex vex = (Vex) (Object) this;
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.EVOKER_VEX_AGRO)) {
            Entity owner = vex.getOwner();
            if (owner instanceof Evoker evoker && evoker.getTarget() instanceof Player player) {
                vex.setTarget(player);
                vex.getNavigation().moveTo(player, 1.0);
            }
        }
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.VEX_LONGER_LIFE)) {
            vex.setLimitedLife(config.getInt("evoker_vex_agro", "value", 1200));
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void ensureAggroAndLife(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        Vex vex = (Vex) (Object) this;
        Level level = vex.level();
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.EVOKER_VEX_AGRO) && vex.getTarget() == null) {
            Entity owner = vex.getOwner();
            if (owner instanceof Evoker evoker && evoker.getTarget() instanceof Player player) {
                vex.setTarget(player);
            } else {
                Player nearest = serverLevel.getNearestPlayer(vex, config.getFloat("evoker_vex_agro", "value1", 16.0f));
                if (nearest != null) {
                    vex.setTarget(nearest);
                }
            }
        }
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.VEX_LONGER_LIFE)) {
            vex.setLimitedLife(config.getInt("evoker_vex_agro", "value1", 1200));
        }
    }
}
