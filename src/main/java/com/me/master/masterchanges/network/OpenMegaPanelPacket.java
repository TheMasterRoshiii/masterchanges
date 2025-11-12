package com.me.master.masterchanges.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenMegaPanelPacket {

    public OpenMegaPanelPacket() {}

    public OpenMegaPanelPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                try {
                    Class<?> handlerClass = Class.forName("com.me.master.masterchanges.screen.ClientScreenHandler");
                    handlerClass.getMethod("openMegaPanel").invoke(null);
                } catch (Exception e) {
                    System.err.println("Failed to open MegaPanel: " + e.getMessage());
                }
            });
        });
        context.setPacketHandled(true);
        return true;
    }
}
