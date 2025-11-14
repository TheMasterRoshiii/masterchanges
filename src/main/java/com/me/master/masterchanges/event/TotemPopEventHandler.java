package com.me.master.masterchanges.event;

import com.me.master.masterchanges.MasterChanges;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MasterChanges.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TotemPopEventHandler {

    @SubscribeEvent
    public static void onTotemPop(LivingUseTotemEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            String totemName = event.getTotem().getHoverName().getString();
            Component message = Component.literal(player.getName().getString() + " ha popeado " + totemName)
                    .withStyle(ChatFormatting.RED);
            player.getServer().getPlayerList().broadcastSystemMessage(message, false);
        }
    }
}
