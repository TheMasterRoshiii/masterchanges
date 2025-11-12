package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import com.me.master.masterchanges.config.MixinConfigManager;
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
    private void createCircleOfFangs(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.EVOKER_CIRCLE_FANGS)) return;

        try {
            java.lang.reflect.Field evokerField = this.getClass().getDeclaredField("this$0");
            evokerField.setAccessible(true);
            Evoker evoker = (Evoker) evokerField.get(this);

            LivingEntity target = evoker.getTarget();
            Level level = evoker.level();

            if (target != null && !level.isClientSide) {
                double targetX = target.getX();
                double targetY = target.getY();
                double targetZ = target.getZ();

                int numFangs = config.getInt("evoker_circle_fangs", "fangCount", 16);
                double radius = config.getDouble("evoker_circle_fangs", "radius", 3.0);
                int delayPerFang = config.getInt("evoker_circle_fangs", "delayPerFang", 2);

                for (int i = 0; i < numFangs; i++) {
                    double angle = (Math.PI * 2 * i) / numFangs;
                    double x = targetX + Math.cos(angle) * radius;
                    double z = targetZ + Math.sin(angle) * radius;

                    int delay = i * delayPerFang;

                    EvokerFangs fangs = new EvokerFangs(level, x, targetY, z,
                            (float) Math.toDegrees(angle), delay, evoker);
                    level.addFreshEntity(fangs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
