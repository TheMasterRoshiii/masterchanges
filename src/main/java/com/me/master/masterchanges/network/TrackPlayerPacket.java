package com.me.master.masterchanges.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TrackPlayerPacket {
    private final String playerName;

    public TrackPlayerPacket(String playerName) {
        this.playerName = playerName;
    }

    public TrackPlayerPacket(FriendlyByteBuf buf) {
        this.playerName = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.playerName);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        });
        ctx.get().setPacketHandled(true);
    }

    public String getPlayerName() {
        return playerName;
    }
}
