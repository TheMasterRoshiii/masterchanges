package com.me.master.masterchanges.event;

import com.me.master.masterchanges.MasterChanges;
import com.me.master.masterchanges.item.ModItems;
import com.me.master.masterchanges.network.ModNetworking;
import com.me.master.masterchanges.network.TotemPopPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MasterChanges.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TotemEventHandler {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        ItemStack totemUsed = ItemStack.EMPTY;


        if (isNormalTotem(mainHand)) {
            totemUsed = mainHand.copy();
            activateNormalTotem(player, mainHand);
            event.setCanceled(true);
        } else if (isNormalTotem(offHand)) {
            totemUsed = offHand.copy();
            activateNormalTotem(player, offHand);
            event.setCanceled(true);
        }

        else if (mainHand.is(ModItems.TOTEM_OWL.get())) {
            totemUsed = mainHand.copy();
            activateOwlTotem(player, mainHand);
            event.setCanceled(true);
        } else if (offHand.is(ModItems.TOTEM_OWL.get())) {
            totemUsed = offHand.copy();
            activateOwlTotem(player, offHand);
            event.setCanceled(true);
        }


        if (!totemUsed.isEmpty() && player instanceof ServerPlayer serverPlayer) {
            ModNetworking.sendToPlayer(new TotemPopPacket(totemUsed), serverPlayer);
        }
    }

    private static boolean isNormalTotem(ItemStack stack) {
        return stack.is(ModItems.NUTRITOTEM.get()) ||
                stack.is(ModItems.TOTEM_ESPECIAL.get()) ||
                stack.is(ModItems.TOTEM_MARIPOSA.get()) ||
                stack.is(ModItems.TOTEM_REVIL.get()) ||
                stack.is(ModItems.TOTEM_EON.get());
    }

    private static void activateNormalTotem(Player player, ItemStack totem) {
        player.setHealth(1.0F);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        totem.shrink(1);


    }

    private static void activateOwlTotem(Player player, ItemStack totem) {
        player.setHealth(1.0F);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 600, 0));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 900, 0));
        totem.shrink(1);
    }
}
