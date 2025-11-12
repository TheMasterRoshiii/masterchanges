package com.me.master.masterchanges.client;

import com.me.master.masterchanges.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientTotemHandler {
    
    private static ItemStack customTotemToRender = ItemStack.EMPTY;
    private static int totemRenderTimer = 0;
    
    public static void setCustomTotemRender(ItemStack stack) {
        customTotemToRender = stack.copy();
        totemRenderTimer = 40;
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event) {
        if (totemRenderTimer > 0) {
            totemRenderTimer--;
            if (totemRenderTimer == 0) {
                customTotemToRender = ItemStack.EMPTY;
            }
        }
    }
}
