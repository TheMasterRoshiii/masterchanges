package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.entity.monster.Evoker$EvokerAttackSpellGoal")
public class EvokerFangsMixin {
    @Inject(method = "performSpellCasting", at = @At("TAIL"))
    private void onCastFangs(CallbackInfo ci) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.EVOKER_CIRCLE_FANGS)) {
            try {
                java.lang.reflect.Field evokerField = this.getClass().getDeclaredField("this$0");
                evokerField.setAccessible(true);
                Evoker evoker = (Evoker) evokerField.get(this);
                LivingEntity target = evoker.getTarget();
                Level level = evoker.level();
                if (target != null && !level.isClientSide) {
                    for (int i = 0; i < 8; i++) {
                        double angle = (Math.PI * 2 * i) / 8;
                        double x = target.getX() + Math.cos(angle) * 3;
                        double z = target.getZ() + Math.sin(angle) * 3;
                        EvokerFangs fangs = new EvokerFangs(level, x, target.getY(), z, 
                                                             (float) angle, 0, evoker);
                        level.addFreshEntity(fangs);
                    }
                }
            } catch (Exception e) {}
        }
    }
}
