package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Random;

@Mixin(ItemStack.class)
public class ItemMixin {
    private static final Random RANDOM = new Random();
    
    @ModifyVariable(method = "hurt(ILnet/minecraft/util/RandomSource;Lnet/minecraft/server/level/ServerPlayer;)Z", 
                    at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int modifyDamageAmount(int amount) {
        if (DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.FAST_DURABILITY_LOSS)) {
            return amount * (2 + RANDOM.nextInt(2));
        }
        return amount;
    }
}
