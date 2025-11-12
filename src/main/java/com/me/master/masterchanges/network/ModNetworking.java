package com.me.master.masterchanges.network;

import com.me.master.masterchanges.MasterChanges;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(MasterChanges.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(OpenMegaPanelPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenMegaPanelPacket::new)
                .encoder(OpenMegaPanelPacket::toBytes)
                .consumerMainThread(OpenMegaPanelPacket::handle)
                .add();

        net.messageBuilder(SyncConfigPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SyncConfigPacket::new)
                .encoder(SyncConfigPacket::toBytes)
                .consumerMainThread(SyncConfigPacket::handle)
                .add();

        net.messageBuilder(TotemPopPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(TotemPopPacket::new)
                .encoder(TotemPopPacket::toBytes)
                .consumerMainThread(TotemPopPacket::handle)
                .add();

    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
