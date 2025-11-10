package com.me.master.masterchanges.event;

import com.me.master.masterchanges.MasterChanges;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MasterChanges.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TotemPopEventHandler {

    @SubscribeEvent
    public static void onTotemPop(LivingUseTotemEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            String message = player.getName().getString() + " ha popeado totem";
            player.getServer().getPlayerList().broadcastSystemMessage(Component.literal(message), false);
        }
    }
}
