package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {
    
    @Inject(method = "getTickDelay", at = @At("RETURN"), cancellable = true)
    private void onGetTickDelay(net.minecraft.world.level.LevelReader level, CallbackInfoReturnable<Integer> cir) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.FAST_LAVA)) {
            int originalDelay = cir.getReturnValue();
            cir.setReturnValue(originalDelay / 3);
        }
    }
}
