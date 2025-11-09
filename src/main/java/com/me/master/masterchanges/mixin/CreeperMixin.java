package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public class CreeperMixin {
    @Unique
    private boolean masterchanges$hasChecked = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Creeper creeper = (Creeper) (Object) this;

        if (!creeper.level().isClientSide &&
                !masterchanges$hasChecked &&
                DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.ELECTRIC_CREEPERS)) {

            masterchanges$hasChecked = true;

            if (creeper.level().random.nextFloat() < 0.50f) {
                CompoundTag tag = new CompoundTag();
                creeper.addAdditionalSaveData(tag);
                tag.putBoolean("powered", true);
                creeper.readAdditionalSaveData(tag);
            }
        }
    }
}
