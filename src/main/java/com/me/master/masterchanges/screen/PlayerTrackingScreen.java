package com.me.master.masterchanges.screen;

import com.me.master.masterchanges.network.ModNetworking;
import com.me.master.masterchanges.network.TrackPlayerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerTrackingScreen extends Screen {
    private final Screen parent;
    private EditBox searchBox;
    private String searchQuery = "";
    private int scrollOffset = 0;
    private final int PLAYERS_PER_PAGE = 15;
    private List<Button> playerButtons = new ArrayList<>();
    private String currentTracking = "";

    public PlayerTrackingScreen(Screen parent) {
        super(Component.literal("Player Tracking"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;

        this.searchBox = new EditBox(this.font, centerX - 100, 30, 200, 18, Component.literal("Buscar jugador..."));
        this.searchBox.setMaxLength(16);
        this.addRenderableWidget(this.searchBox);

        this.addRenderableWidget(Button.builder(
                        Component.literal("§cDetener Tracking"),
                        btn -> {
                            currentTracking = "";
                            ModNetworking.sendToServer(new TrackPlayerPacket(""));
                            rebuildPlayerList();
                        })
                .bounds(centerX - 60, 55, 120, 18).build());

        this.addRenderableWidget(Button.builder(
                        Component.literal("▲"),
                        btn -> { if (scrollOffset > 0) { scrollOffset--; rebuildPlayerList(); } })
                .bounds(this.width - 25, 80, 20, 18).build());

        this.addRenderableWidget(Button.builder(
                        Component.literal("▼"),
                        btn -> { if (scrollOffset < getMaxScroll()) { scrollOffset++; rebuildPlayerList(); } })
                .bounds(this.width - 25, 102, 20, 18).build());

        this.addRenderableWidget(Button.builder(
                        Component.literal("Volver"),
                        btn -> this.minecraft.setScreen(parent))
                .bounds(centerX - 40, this.height - 25, 80, 20).build());

        rebuildPlayerList();
    }

    private void rebuildPlayerList() {
        playerButtons.forEach(this::removeWidget);
        playerButtons.clear();

        List<String> players = getFilteredPlayers();
        int yPos = 80;
        int centerX = this.width / 2;

        int end = Math.min(scrollOffset + PLAYERS_PER_PAGE, players.size());
        for (int i = scrollOffset; i < end; i++) {
            String playerName = players.get(i);
            boolean isTracking = playerName.equals(currentTracking);
            String prefix = isTracking ? "§a► " : "§7";

            Button playerBtn = Button.builder(
                            Component.literal(prefix + playerName),
                            btn -> {
                                currentTracking = playerName;
                                ModNetworking.sendToServer(new TrackPlayerPacket(playerName));
                                rebuildPlayerList();
                            })
                    .bounds(centerX - 100, yPos, 200, 18).build();

            playerButtons.add(playerBtn);
            this.addRenderableWidget(playerBtn);
            yPos += 22;
        }
    }

    private List<String> getFilteredPlayers() {
        List<String> players = new ArrayList<>();
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        
        if (connection != null) {
            Collection<PlayerInfo> playerInfos = connection.getOnlinePlayers();
            for (PlayerInfo info : playerInfos) {
                String name = info.getProfile().getName();
                if (searchQuery.isEmpty() || name.toLowerCase().contains(searchQuery.toLowerCase())) {
                    players.add(name);
                }
            }
        }
        
        players.sort(String::compareToIgnoreCase);
        return players;
    }

    private int getMaxScroll() {
        return Math.max(0, getFilteredPlayers().size() - PLAYERS_PER_PAGE);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, "§6§lPLAYER TRACKING", this.width / 2, 10, 0xFFD700);
        
        if (!currentTracking.isEmpty()) {
            guiGraphics.drawCenteredString(this.font, "§aTracking: §f" + currentTracking, this.width / 2, this.height - 40, 0xFFFFFF);
        } else {
            guiGraphics.drawCenteredString(this.font, "§7No estás trackeando a nadie", this.width / 2, this.height - 40, 0xFFFFFF);
        }
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void tick() {
        super.tick();
        if (searchBox != null) {
            searchBox.tick();
            String newQuery = searchBox.getValue();
            if (!newQuery.equals(searchQuery)) {
                searchQuery = newQuery;
                scrollOffset = 0;
                rebuildPlayerList();
            }
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
