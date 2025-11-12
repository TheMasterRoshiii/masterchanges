package com.me.master.masterchanges.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TotemPopPacket {
    private final ItemStack totemStack;
    
    public TotemPopPacket(ItemStack stack) {
        this.totemStack = stack;
    }
    
    public TotemPopPacket(FriendlyByteBuf buf) {
        this.totemStack = buf.readItem();
    }
    
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(totemStack);
    }
    
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    mc.gameRenderer.displayItemActivation(totemStack);
                }
            });
        });
        context.setPacketHandled(true);
        return true;
    }
}
