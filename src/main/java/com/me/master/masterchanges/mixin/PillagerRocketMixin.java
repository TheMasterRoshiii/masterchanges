package com.me.master.masterchanges.mixin;

import com.google.common.collect.Maps;
import com.me.master.masterchanges.config.MixinConfigManager;
import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Pillager.class)
public class PillagerRocketMixin {

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnReason,
                                 net.minecraft.world.entity.SpawnGroupData spawnData, CompoundTag dataTag,
                                 CallbackInfoReturnable<net.minecraft.world.entity.SpawnGroupData> cir) {
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PILLAGER_ROCKET)) return;

        MixinConfigManager config = MixinConfigManager.getInstance();
        Pillager pillager = (Pillager)(Object)this;

        ItemStack crossbow = new ItemStack(Items.CROSSBOW);
        Map<Enchantment, Integer> enchantments = Maps.newHashMap();
        enchantments.put(Enchantments.MULTISHOT, config.getInt("pillager_rocket", "multishotLevel", 1));
        enchantments.put(Enchantments.QUICK_CHARGE, config.getInt("pillager_rocket", "quickChargeLevel", 3));
        EnchantmentHelper.setEnchantments(enchantments, crossbow);
        pillager.setItemSlot(EquipmentSlot.MAINHAND, crossbow);

        int rocketCount = config.getInt("pillager_rocket", "rocketCount", 64);
        int explosionPower = config.getInt("pillager_rocket", "explosionPower", 3);

        ItemStack rockets = createExplosiveRocket(explosionPower);
        rockets.setCount(rocketCount);
        pillager.setItemSlot(EquipmentSlot.OFFHAND, rockets);
    }

    private ItemStack createExplosiveRocket(int power) {
        ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fireworksTag = new CompoundTag();
        CompoundTag explosionsTag = new CompoundTag();

        ListTag explosionsList = new ListTag();
        CompoundTag explosion = new CompoundTag();
        explosion.putByte("Type", (byte) 1);
        explosion.putIntArray("Colors", new int[]{16711680, 16744448});
        explosion.putIntArray("FadeColors", new int[]{16755200});
        explosionsList.add(explosion);

        explosionsTag.put("Explosions", explosionsList);
        explosionsTag.putByte("Flight", (byte) power);

        fireworksTag.put("Fireworks", explosionsTag);
        rocket.setTag(fireworksTag);

        return rocket;
    }
}
