package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Vex;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(targets = "net.minecraft.world.entity.monster.Evoker$EvokerSummonSpellGoal")
public class EvokerMixin {

    @Inject(method = "performSpellCasting", at = @At("TAIL"))
    private void onPerformSpellCasting(CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.EVOKER_TRIPLE_VEX)) return;

        try {
            java.lang.reflect.Field evokerField = this.getClass().getDeclaredField("this$0");
            evokerField.setAccessible(true);
            Evoker evoker = (Evoker) evokerField.get(this);
            Level level = evoker.level();

            if (!level.isClientSide) {
                for (int i = 0; i < config.getInt("evoker_triple_vex", "count1", 2); i++) {
                    Vex vex = new Vex(net.minecraft.world.entity.EntityType.VEX, level);
                    vex.moveTo(evoker.getX(), evoker.getY() + 1, evoker.getZ(), 0, 0);
                    vex.setOwner(evoker);
                    if (evoker.getTarget() != null) {
                        vex.setTarget(evoker.getTarget());
                    }
                    level.addFreshEntity(vex);
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
