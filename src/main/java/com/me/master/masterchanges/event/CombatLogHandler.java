package com.me.master.masterchanges.event;

import com.me.master.masterchanges.MasterChanges;
import com.me.master.masterchanges.combat.CombatLogManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MasterChanges.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CombatLogHandler {

    @SubscribeEvent
    public static void onPlayerAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (event.getEntity() instanceof Mob) {
                CombatLogManager.getInstance().enterCombat(player);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(
                            Component.literal("§cEstás en combate! No salgas por 30 segundos."),
                            true
                    );
                }
            }
        }

        if (event.getEntity() instanceof Player player) {
            if (event.getSource().getEntity() instanceof Mob) {
                CombatLogManager.getInstance().enterCombat(player);
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(
                            Component.literal("§cEstás en combate! No salgas por 30 segundos."),
                            true
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer serverPlayer)) return;

        long remainingTime = CombatLogManager.getInstance().getRemainingCombatTime(event.player);
        if (remainingTime > 0) {
            int seconds = (int) (remainingTime / 1000);
            if (seconds % 5 == 0 && event.player.tickCount % 20 == 0) {
                serverPlayer.displayClientMessage(
                        Component.literal("§cCombat: " + seconds + "s"),
                        true
                );
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();

        if (CombatLogManager.getInstance().isInCombat(player)) {
            player.kill();
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.getServer() != null) {
                serverPlayer.getServer().getPlayerList().broadcastSystemMessage(
                        Component.literal(player.getName().getString() + " hizo combat log y murió")
                                .withStyle(ChatFormatting.RED),
                        false
                );
            }
        }

        CombatLogManager.getInstance().removeCombat(player.getUUID());
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            CombatLogManager.getInstance().removeCombat(event.getEntity().getUUID());
        }
    }
}
