package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CactusBlock.class)
public class CactusMixin {
    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void insaneCactusDamage(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.CACTUS_INSANE_DAMAGE)) {
            if (entity instanceof LivingEntity living) {
                entity.hurt(level.damageSources().cactus(), 9000000.0F);

                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                        ItemStack armor = living.getItemBySlot(slot);
                        if (!armor.isEmpty()) {
                            armor.shrink(armor.getCount());
                            living.setItemSlot(slot, ItemStack.EMPTY);
                        }
                    }
                }
                ci.cancel();
            }
        }
    }
}
