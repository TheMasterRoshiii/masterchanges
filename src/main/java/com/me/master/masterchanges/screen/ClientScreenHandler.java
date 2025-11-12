package com.me.master.masterchanges.screen;

import com.me.master.masterchanges.screen.MegaPanelScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientScreenHandler {
    
    public static void openMegaPanel() {
        Minecraft.getInstance().setScreen(new MegaPanelScreen());
    }
}
