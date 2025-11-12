package com.me.master.masterchanges.screen;

import com.me.master.masterchanges.config.MixinConfig;
import com.me.master.masterchanges.config.MixinConfigManager;
import com.me.master.masterchanges.network.ModNetworking;
import com.me.master.masterchanges.network.SyncConfigPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.*;

public class MegaPanelScreen extends Screen {
    private final List<MixinConfig> allConfigs;
    private int scrollOffset = 0;
    private final int CONFIGS_PER_PAGE = 12;
    private String searchQuery = "";
    private EditBox searchBox;
    private List<Button> configButtons = new ArrayList<>();

    private enum Category {
        ALL("Todos"),
        SPAWNING("Spawning"),
        MOB_DAMAGE("Daño Mobs"),
        MOB_ABILITIES("Habilidades"),
        BLOCKS("Bloques"),
        ENVIRONMENT("Entorno"),
        PLAYER("Jugador"),
        VILLAGES("Aldeanos");
        final String name;
        Category(String name) { this.name = name; }
    }
    private Category currentCategory = Category.ALL;

    public MegaPanelScreen() {
        super(Component.literal("MEGA PANEL"));
        this.allConfigs = new ArrayList<>(MixinConfigManager.getInstance().getAllConfigs());
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;

        this.searchBox = new EditBox(this.font, centerX - 100, 30, 200, 18, Component.literal("Buscar..."));
        this.searchBox.setMaxLength(50);
        this.addRenderableWidget(this.searchBox);

        int catX = 10;
        int catY = 60;
        int catWidth = 80;

        for (Category cat : Category.values()) {
            Category finalCat = cat;
            this.addRenderableWidget(Button.builder(
                            Component.literal(cat.name),
                            btn -> { currentCategory = finalCat; scrollOffset = 0; rebuildConfigList(); })
                    .bounds(catX, catY, catWidth, 18).build());
            catY += 22;
        }

        this.addRenderableWidget(Button.builder(
                        Component.literal("§a✓ ON"),
                        btn -> toggleAllMixins(true))
                .bounds(this.width - 90, 30, 80, 18).build());

        this.addRenderableWidget(Button.builder(
                        Component.literal("§c✗ OFF"),
                        btn -> toggleAllMixins(false))
                .bounds(this.width - 90, 52, 80, 18).build());

        this.addRenderableWidget(Button.builder(
                        Component.literal("▲"),
                        btn -> { if (scrollOffset > 0) { scrollOffset--; rebuildConfigList(); } })
                .bounds(this.width - 25, 80, 20, 18).build());

        this.addRenderableWidget(Button.builder(
                        Component.literal("▼"),
                        btn -> { if (scrollOffset < getMaxScroll()) { scrollOffset++; rebuildConfigList(); } })
                .bounds(this.width - 25, 102, 20, 18).build());

        this.addRenderableWidget(Button.builder(
                        Component.literal("Cerrar"),
                        btn -> this.onClose())
                .bounds(centerX - 40, this.height - 25, 80, 20).build());

        rebuildConfigList();
    }

    private void rebuildConfigList() {
        configButtons.forEach(this::removeWidget);
        configButtons.clear();

        List<MixinConfig> filtered = getFilteredConfigs();
        int yPos = 80;
        int xStart = 100;
        int width = this.width - 130;

        int end = Math.min(scrollOffset + CONFIGS_PER_PAGE, filtered.size());
        for (int i = scrollOffset; i < end; i++) {
            MixinConfig config = filtered.get(i);
            String statusColor = config.isEnabled() ? "§a" : "§7";
            String statusSymbol = config.isEnabled() ? "✓" : "✗";

            String displayName = config.getDisplayName();
            if (displayName.length() > 35) {
                displayName = displayName.substring(0, 32) + "...";
            }

            Button toggleBtn = Button.builder(
                            Component.literal(statusColor + statusSymbol + " " + displayName),
                            btn -> {
                                config.setEnabled(!config.isEnabled());
                                syncConfig(config);
                                rebuildConfigList();
                            })
                    .bounds(xStart, yPos, width - 30, 18).build();

            Button editBtn = Button.builder(
                            Component.literal("§e⚙"),
                            btn -> this.minecraft.setScreen(new ConfigEditorScreen(this, config)))
                    .bounds(xStart + width - 25, yPos, 25, 18).build();

            configButtons.add(toggleBtn);
            configButtons.add(editBtn);
            this.addRenderableWidget(toggleBtn);
            this.addRenderableWidget(editBtn);

            yPos += 22;
        }
    }

    private void syncConfig(MixinConfig config) {
        Map<String, String> params = new HashMap<>();
        config.getAllParameters().forEach((key, value) -> params.put(key, String.valueOf(value)));
        ModNetworking.sendToServer(new SyncConfigPacket(config.getId(), config.isEnabled(), params));
    }

    private List<MixinConfig> getFilteredConfigs() {
        return allConfigs.stream().filter(config -> {
            if (!searchQuery.isEmpty() &&
                    !config.getDisplayName().toLowerCase().contains(searchQuery.toLowerCase()) &&
                    !config.getId().toLowerCase().contains(searchQuery.toLowerCase())) {
                return false;
            }
            if (currentCategory == Category.ALL) return true;
            String id = config.getId().toLowerCase();
            return switch (currentCategory) {
                case SPAWNING -> id.contains("spawn") || id.contains("light_level");
                case MOB_DAMAGE -> id.contains("damage") || id.contains("triple") || id.contains("insane");
                case MOB_ABILITIES -> id.contains("teleport") || id.contains("steal") || id.contains("multi") || id.contains("fast") || id.contains("triple_shot") || id.contains("double_fireball") || id.contains("hostile") || id.contains("ai") || id.contains("freeze") || id.contains("immobilize") || id.contains("horde");
                case BLOCKS -> id.contains("button") || id.contains("door") || id.contains("lever") || id.contains("trapdoor") || id.contains("bed") || id.contains("cactus") || id.contains("cobweb");
                case ENVIRONMENT -> id.contains("rain") || id.contains("fire") || id.contains("lava") || id.contains("water") || id.contains("drown");
                case PLAYER -> id.contains("inventory") || id.contains("armor") || id.contains("durability") || id.contains("shield") || id.contains("enchantment") || id.contains("totem") || id.contains("fall");
                case VILLAGES -> id.contains("villager");
                default -> true;
            };
        }).toList();
    }

    private int getMaxScroll() {
        return Math.max(0, getFilteredConfigs().size() - CONFIGS_PER_PAGE);
    }

    private void toggleAllMixins(boolean enabled) {
        getFilteredConfigs().forEach(config -> {
            config.setEnabled(enabled);
            syncConfig(config);
        });
        rebuildConfigList();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, "§6§lMEGA PANEL DE CONFIGURACIÓN", this.width / 2, 10, 0xFFD700);
        guiGraphics.drawString(this.font, "§7Cat: §e" + currentCategory.name, 10, 40, 0xFFFFFF);
        List<MixinConfig> filtered = getFilteredConfigs();
        int showing = Math.min(scrollOffset + CONFIGS_PER_PAGE, filtered.size());
        String counter = "§f" + showing + "§7/§f" + filtered.size();
        guiGraphics.drawString(this.font, counter, this.width - 80, this.height - 10, 0xFFFFFF);
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
                rebuildConfigList();
            }
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
