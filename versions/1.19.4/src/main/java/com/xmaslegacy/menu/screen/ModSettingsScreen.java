package com.xmaslegacy.menu.screen;

import com.xmaslegacy.menu.config.ModConfig;
import com.xmaslegacy.menu.render.SnowParticleRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModSettingsScreen extends Screen {
    private final Screen parent;

    public ModSettingsScreen(Screen parent) {
        super(Component.literal("XmasLegacy Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int startY = this.height / 2 - 60;
        int spacing = 28;
        int leftX = centerX - buttonWidth / 2;

        this.addRenderableWidget(Button.builder(
            Component.literal("Snow Particles: " + (ModConfig.snowEnabled ? "ON" : "OFF")),
            button -> {
                ModConfig.snowEnabled = !ModConfig.snowEnabled;
                button.setMessage(Component.literal("Snow Particles: " + (ModConfig.snowEnabled ? "ON" : "OFF")));
            }
        ).bounds(leftX, startY, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
            Component.literal("Particle Count: " + ModConfig.particleCount),
            button -> {
                if (ModConfig.particleCount >= 200) {
                    ModConfig.particleCount = 50;
                } else {
                    ModConfig.particleCount += 50;
                }
                button.setMessage(Component.literal("Particle Count: " + ModConfig.particleCount));
                SnowParticleRenderer.forceReinit();
            }
        ).bounds(leftX, startY + spacing, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
            Component.literal("Tab Snowflake: " + (ModConfig.tabSnowflakeEnabled ? "ON" : "OFF")),
            button -> {
                ModConfig.tabSnowflakeEnabled = !ModConfig.tabSnowflakeEnabled;
                button.setMessage(Component.literal("Tab Snowflake: " + (ModConfig.tabSnowflakeEnabled ? "ON" : "OFF")));
            }
        ).bounds(leftX, startY + spacing * 2, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(
            Component.literal("Done"),
            button -> this.minecraft.setScreen(this.parent)
        ).bounds(leftX, startY + spacing * 3 + 16, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xFF0A0F14, 0xFF0C1A12);

        int panelWidth = 240;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = this.height / 2 - 90;
        int panelHeight = 180;
        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0x40000000);
        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + 1, 0x20FFFFFF);
        guiGraphics.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, 0x20FFFFFF);
        guiGraphics.fill(panelX, panelY, panelX + 1, panelY + panelHeight, 0x20FFFFFF);
        guiGraphics.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, 0x20FFFFFF);

        guiGraphics.drawCenteredString(this.font, "XmasLegacy Settings", this.width / 2, panelY + 10, 0xFFE5C158);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}
