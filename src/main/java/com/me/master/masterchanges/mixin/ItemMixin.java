package com.me.master.masterchanges.mixin;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import com.me.master.masterchanges.config.MixinConfigManager;
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
            MixinConfigManager config = MixinConfigManager.getInstance();
            int multiplierMin = config.getInt("fast_durability_loss", "multiplierMin", 2);
            int multiplierExtra = config.getInt("fast_durability_loss", "multiplierExtra", 2);
            return amount * (multiplierMin + RANDOM.nextInt(multiplierExtra));
        }
        return amount;
    }
}
