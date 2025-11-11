package com.me.master.masterchanges.event;

import com.me.master.masterchanges.MasterChanges;
import com.me.master.masterchanges.client.BuhCatTotemOverlay;
import com.me.master.masterchanges.item.BuhCatTotemItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.renderer.GameRenderer;
@Mod.EventBusSubscriber(modid = MasterChanges.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TotemDisplayEventHandler {
    
    private static int deathCheckCooldown = 0;
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        Player player = mc.player;
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        
        boolean holdingBuhCat = mainHand.getItem() instanceof BuhCatTotemItem || 
                                offHand.getItem() instanceof BuhCatTotemItem;
        
        if (holdingBuhCat && player.getHealth() <= 0.0F && deathCheckCooldown == 0) {
            ItemStack totemStack = mainHand.getItem() instanceof BuhCatTotemItem ? mainHand : offHand;
            BuhCatTotemOverlay.setActiveTotem(totemStack);
            deathCheckCooldown = 100;
        }
        
        if (deathCheckCooldown > 0) {
            deathCheckCooldown--;
        }
    }
}
