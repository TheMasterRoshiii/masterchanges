package com.me.master.masterchanges.event;

import com.me.master.masterchanges.MasterChanges;
import com.me.master.masterchanges.client.BuhCatTotemOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MasterChanges.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TotemRenderHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            BuhCatTotemOverlay.tick();
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (BuhCatTotemOverlay.isActive()) {
            BuhCatTotemOverlay.render(event.getGuiGraphics().pose(), event.getPartialTick());
        }
    }
}
