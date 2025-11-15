package com.me.master.masterchanges.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MobcapConfigPacket {
    private final Map<String, Integer> mobcaps;
    private final Map<String, Boolean> allowedMobs;

    public MobcapConfigPacket(Map<String, Integer> mobcaps, Map<String, Boolean> allowedMobs) {
        this.mobcaps = mobcaps;
        this.allowedMobs = allowedMobs;
    }

    public MobcapConfigPacket(FriendlyByteBuf buf) {
        int mobcapSize = buf.readInt();
        this.mobcaps = new HashMap<>();
        for (int i = 0; i < mobcapSize; i++) {
            mobcaps.put(buf.readUtf(), buf.readInt());
        }

        int allowedSize = buf.readInt();
        this.allowedMobs = new HashMap<>();
        for (int i = 0; i < allowedSize; i++) {
            allowedMobs.put(buf.readUtf(), buf.readBoolean());
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(mobcaps.size());
        mobcaps.forEach((key, value) -> {
            buf.writeUtf(key);
            buf.writeInt(value);
        });

        buf.writeInt(allowedMobs.size());
        allowedMobs.forEach((key, value) -> {
            buf.writeUtf(key);
            buf.writeBoolean(value);
        });
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        });
        ctx.get().setPacketHandled(true);
    }

    public Map<String, Integer> getMobcaps() {
        return mobcaps;
    }

    public Map<String, Boolean> getAllowedMobs() {
        return allowedMobs;
    }
}
