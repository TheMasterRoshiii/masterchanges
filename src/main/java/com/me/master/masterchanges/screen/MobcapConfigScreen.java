package com.me.master.masterchanges.screen;

import com.me.master.masterchanges.config.MobcapConfig;
import com.me.master.masterchanges.network.ModNetworking;
import com.me.master.masterchanges.network.MobcapConfigPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class   MobcapConfigScreen extends Screen {
    private final Screen parent;
    private final Map<String, EditBox> editBoxes = new HashMap<>();
    private final Map<String, Button> toggleButtons = new HashMap<>();

    public MobcapConfigScreen(Screen parent) {
        super(Component.literal("Mobcap Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;
        int yPos = 60;

        MobcapConfig config = MobcapConfig.getInstance();

        for (String mobType : config.getMobcaps().keySet()) {
            int currentValue = config.getMobcaps().get(mobType);

            EditBox box = new EditBox(this.font, centerX - 40, yPos, 80, 18, Component.literal(mobType));
            box.setValue(String.valueOf(currentValue));
            box.setMaxLength(4);
            editBoxes.put(mobType, box);
            this.addRenderableWidget(box);

            yPos += 25;
        }

        yPos += 20;

        for (String mobName : config.getAllowedMobs().keySet()) {
            boolean allowed = config.getAllowedMobs().get(mobName);

            Button btn = Button.builder(
                            Component.literal((allowed ? "§a✓ " : "§c✗ ") + capitalize(mobName) + (allowed ? " §7(Spawn)" : " §7(No Spawn)")),
                            button -> {
                                config.setMobAllowed(mobName, !config.getAllowedMobs().get(mobName));
                                rebuildButtons();
                            })
                    .bounds(centerX - 100, yPos, 200, 18).build();

            toggleButtons.put(mobName, btn);
            this.addRenderableWidget(btn);

            yPos += 22;
        }

        yPos += 20;
        this.addRenderableWidget(Button.builder(
                        Component.literal("§aGuardar y Aplicar"),
                        btn -> {
                            applyChanges();
                            this.minecraft.setScreen(parent);
                        })
                .bounds(centerX - 80, yPos, 160, 20).build());

        this.addRenderableWidget(Button.builder(
                        Component.literal("Cancelar"),
                        btn -> this.minecraft.setScreen(parent))
                .bounds(centerX - 40, this.height - 30, 80, 20).build());
    }

    private void rebuildButtons() {
        this.clearWidgets();
        this.init();
    }

    private void applyChanges() {
        MobcapConfig config = MobcapConfig.getInstance();

        editBoxes.forEach((type, box) -> {
            try {
                int value = Integer.parseInt(box.getValue());
                config.setMobcap(type, Math.max(0, Math.min(1000, value)));
            } catch (NumberFormatException ignored) {
            }
        });

        config.save();
        ModNetworking.sendToServer(new MobcapConfigPacket(config.getMobcaps(), config.getAllowedMobs()));
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).replace("_", " ");
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int yPos = 60;

        guiGraphics.drawCenteredString(this.font, "§6§lCONFIGURACIÓN DE MOBCAP", centerX, 15, 0xFFD700);
        guiGraphics.drawCenteredString(this.font, "§e§lLímites de Spawn", centerX, 40, 0xFFD700);

        MobcapConfig config = MobcapConfig.getInstance();
        for (String mobType : config.getMobcaps().keySet()) {
            guiGraphics.drawString(this.font, "§f" + capitalize(mobType) + ":", centerX - 130, yPos + 5, 0xFFFFFF);
            yPos += 25;
        }

        yPos += 20;
        guiGraphics.drawCenteredString(this.font, "§e§lMobs Especiales", centerX, yPos - 5, 0xFFD700);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
