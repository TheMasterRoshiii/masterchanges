package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(FlowingFluid.class)
public class FlowingFluidMixin {
    
    @Inject(method = "getSpreadDelay", at = @At("RETURN"), cancellable = true)
    private void onGetSpreadDelay(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos, 
                                   net.minecraft.world.level.material.FluidState currentState, 
                                   net.minecraft.world.level.material.FluidState newState, 
                                   CallbackInfoReturnable<Integer> cir) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.FAST_LAVA)) {
            FlowingFluid fluid = (FlowingFluid) (Object) this;
            if (fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA) {
                int originalDelay = cir.getReturnValue();
                cir.setReturnValue(originalDelay / config.getInt("fast_lava", "value", 3));
            }
        }
    }
}
