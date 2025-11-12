package com.me.master.masterchanges.network;

import com.me.master.masterchanges.config.MixinConfigManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncConfigPacket {
    private final String mixinId;
    private final boolean enabled;
    private final Map<String, String> parameters;

    public SyncConfigPacket(String mixinId, boolean enabled, Map<String, String> parameters) {
        this.mixinId = mixinId;
        this.enabled = enabled;
        this.parameters = parameters;
    }

    public SyncConfigPacket(FriendlyByteBuf buf) {
        this.mixinId = buf.readUtf(256);
        this.enabled = buf.readBoolean();
        int size = buf.readInt();
        this.parameters = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String key = buf.readUtf(256);
            String value = buf.readUtf(256);
            this.parameters.put(key, value);
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(mixinId, 256);
        buf.writeBoolean(enabled);
        buf.writeInt(parameters.size());
        parameters.forEach((key, value) -> {
            buf.writeUtf(key, 256);
            buf.writeUtf(value, 256);
        });
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            try {
                MixinConfigManager manager = MixinConfigManager.getInstance();
                manager.setMixinEnabled(mixinId, enabled);
                parameters.forEach((key, value) -> {
                    try {
                        manager.setParameter(mixinId, key, parseValue(value));
                    } catch (Exception e) {
                        System.err.println("Error setting parameter " + key + ": " + e.getMessage());
                    }
                });

                ServerPlayer sender = context.getSender();
                if (sender != null) {
                    sender.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§aConfiguracion actualizada: §f" + mixinId
                    ));
                }
            } catch (Exception e) {
                System.err.println("Error handling sync packet: " + e.getMessage());
                e.printStackTrace();
            }
        });
        context.setPacketHandled(true);
        return true;
    }

    private Object parseValue(String value) {
        if (value == null || value.isEmpty()) return value;
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        }
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            return value;
        }
    }
}
