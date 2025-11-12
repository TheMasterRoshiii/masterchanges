package com.me.master.masterchanges.screen;

import com.me.master.masterchanges.config.MixinConfig;
import com.me.master.masterchanges.network.ModNetworking;
import com.me.master.masterchanges.network.SyncConfigPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.*;

public class ConfigEditorScreen extends Screen {
    private final Screen parent;
    private final MixinConfig config;
    private final Map<String, EditBox> parameterFields = new HashMap<>();
    private int scrollOffset = 0;

    public ConfigEditorScreen(Screen parent, MixinConfig config) {
        super(Component.literal("Configurar: " + config.getDisplayName()));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int yPos = 60;

        this.addRenderableWidget(Button.builder(
                        Component.literal(config.isEnabled() ? "§a✓ Activado" : "§c✗ Desactivado"),
                        btn -> {
                            config.setEnabled(!config.isEnabled());
                            syncConfig();
                            this.clearWidgets();
                            this.init();
                        })
                .bounds(centerX - 100, 30, 200, 20).build());

        List<String> paramKeys = new ArrayList<>(config.getParameterKeys());
        Collections.sort(paramKeys);

        for (String key : paramKeys) {
            Object value = config.getAllParameters().get(key);
            EditBox field = new EditBox(this.font, centerX - 100, yPos, 200, 20, Component.literal(key));
            field.setValue(String.valueOf(value));
            field.setMaxLength(100);
            parameterFields.put(key, field);
            this.addRenderableWidget(field);

            final String finalKey = key;
            this.addRenderableWidget(Button.builder(
                            Component.literal("§aOK"),
                            btn -> {
                                EditBox editBox = parameterFields.get(finalKey);
                                if (editBox != null) {
                                    applyParameter(finalKey, editBox.getValue());
                                    syncConfig();
                                }
                            })
                    .bounds(centerX + 110, yPos, 40, 20).build());
            yPos += 30;
        }

        this.addRenderableWidget(Button.builder(
                        Component.literal("§aGuardar & Cerrar"),
                        btn -> {
                            saveAllParameters();
                            syncConfig();
                            this.minecraft.setScreen(parent);
                        })
                .bounds(centerX - 110, this.height - 30, 100, 20).build());

        this.addRenderableWidget(Button.builder(
                        Component.literal("§cCancelar"),
                        btn -> this.minecraft.setScreen(parent))
                .bounds(centerX + 10, this.height - 30, 100, 20).build());
    }

    private void applyParameter(String key, String value) {
        try {
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                config.setParameter(key, Boolean.parseBoolean(value));
            } else if (value.contains(".")) {
                config.setParameter(key, Double.parseDouble(value));
            } else {
                try {
                    config.setParameter(key, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    config.setParameter(key, value);
                }
            }
        } catch (Exception e) {
            config.setParameter(key, value);
        }
    }

    private void saveAllParameters() {
        for (Map.Entry<String, EditBox> entry : parameterFields.entrySet()) {
            applyParameter(entry.getKey(), entry.getValue().getValue());
        }
    }

    private void syncConfig() {
        Map<String, String> params = new HashMap<>();
        config.getAllParameters().forEach((key, value) -> params.put(key, String.valueOf(value)));
        ModNetworking.sendToServer(new SyncConfigPacket(config.getId(), config.isEnabled(), params));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFD700);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
