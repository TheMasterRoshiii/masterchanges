package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.me.master.masterchanges.config.MixinConfigManager;

@Mixin(MagmaCube.class)
public abstract class MagmaCubeMixin {
    
    @Shadow
    public abstract void setSize(int size, boolean resetHealth);
    
    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
    private void onInit(EntityType<? extends MagmaCube> entityType, Level level, CallbackInfo ci) {
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (!level.isClientSide && DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.MAX_SIZE_SLIMES)) {
            this.setSize(config.getInt("max_size_slimes", "value", 15), true);
        }
    }
}
