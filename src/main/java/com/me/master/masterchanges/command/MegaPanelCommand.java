package com.me.master.masterchanges.command;

import com.me.master.masterchanges.network.ModNetworking;
import com.me.master.masterchanges.network.OpenMegaPanelPacket;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class MegaPanelCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("megapanel")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                ModNetworking.sendToPlayer(new OpenMegaPanelPacket(), player);
                                player.sendSystemMessage(Component.literal("§6§l[MEGA PANEL] §eAbriendo panel de configuración..."));
                                return 1;
                            }
                            context.getSource().sendFailure(Component.literal("Solo puede ser ejecutado por operadores"));
                            return 0;
                        })
        );
    }
}
