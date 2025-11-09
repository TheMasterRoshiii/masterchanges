package com.me.master.masterchanges.event;

import com.me.master.masterchanges.MasterChanges;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MasterChanges.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TotemPopEventHandler {

    @SubscribeEvent
    public static void onTotemPop(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerLevel level = (ServerLevel) player.level();
        
        if (player.getHealth() <= 0.0F) {
            if (player.getMainHandItem().is(Items.TOTEM_OF_UNDYING) || player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) {
                String message = "" + player.getName().getString() + " ha popeado totem";
                level.getServer().getPlayerList().broadcastSystemMessage(Component.literal(message), false);
            }
        }
    }
}
