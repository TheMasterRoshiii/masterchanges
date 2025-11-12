package com.me.master.masterchanges;

import com.me.master.masterchanges.command.DifficultyCommand;
import com.me.master.masterchanges.command.MegaPanelCommand;
import com.me.master.masterchanges.item.ModItems;
import com.me.master.masterchanges.network.ModNetworking;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MasterChanges.MODID)
public class MasterChanges {
    public static final String MODID = "masterchanges";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MasterChanges() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetworking.register();
            LOGGER.info("MasterChanges networking registered");
        });
        LOGGER.info("MasterChanges common setup");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("MasterChanges server starting");
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        DifficultyCommand.register(event.getDispatcher());
        MegaPanelCommand.register(event.getDispatcher());
        LOGGER.info("Comandos registrados");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("MasterChanges client setup");
        }
    }
}
