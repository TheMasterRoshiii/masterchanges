package com.me.master.masterchanges.network;

import com.me.master.masterchanges.screen.MegaPanelScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenMegaPanelPacket {
    
    public OpenMegaPanelPacket() {}
    
    public OpenMegaPanelPacket(FriendlyByteBuf buf) {}
    
    public void toBytes(FriendlyByteBuf buf) {}
    
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new MegaPanelScreen());
        });
        return true;
    }
}
