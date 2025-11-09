package com.me.master.masterchanges.mixin;

import com.google.common.collect.Maps;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ServerLevelAccessor;

import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Pillager.class)
public class PillagerRocketMixin {

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    private void onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnReason,
                                 net.minecraft.world.entity.SpawnGroupData spawnData, CompoundTag dataTag, CallbackInfoReturnable<net.minecraft.world.entity.SpawnGroupData> cir) {
        Pillager pillager = (Pillager)(Object)this;

        ItemStack crossbow = new ItemStack(Items.CROSSBOW);

        Map<Enchantment, Integer> enchantments = Maps.newHashMap();
        enchantments.put(Enchantments.MULTISHOT, 1);
        enchantments.put(Enchantments.QUICK_CHARGE, 1);
        EnchantmentHelper.setEnchantments(enchantments, crossbow);

        pillager.setItemSlot(EquipmentSlot.MAINHAND, crossbow);

        ItemStack rockets = new ItemStack(Items.FIREWORK_ROCKET, 3);
        pillager.getInventory().addItem(rockets);
    }
}
