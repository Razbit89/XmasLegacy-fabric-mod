package com.xmaslegacy.menu.screen;

import com.xmaslegacy.menu.render.SnowParticleRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CosmeticsScreen extends Screen {
    private final Screen parent;
    
    // Cosmetic toggle states (simulated for UI showcase)
    private static boolean capeActive = true;
    private static boolean hatActive = false;
    private static boolean wingsActive = false;
    private static boolean bootsActive = true;

    public CosmeticsScreen(Screen parent) {
        super(Component.literal("Cosmetics"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.clearWidgets();

        // 1. Back Button (bottom center)
        this.addRenderableWidget(Button.builder(
            Component.literal("Back"),
            button -> this.minecraft.setScreen(this.parent)
        ).bounds(this.width / 2 - 50, this.height - 30, 100, 20).build());

        // 2. Left Column: Cosmetic Toggles
        int leftX = 30;
        this.addRenderableWidget(Button.builder(
            Component.literal("Cape: " + (capeActive ? "ON" : "OFF")),
            button -> {
                capeActive = !capeActive;
                button.setMessage(Component.literal("Cape: " + (capeActive ? "ON" : "OFF")));
            }
        ).bounds(leftX, 80, 100, 20).build());

        this.addRenderableWidget(Button.builder(
            Component.literal("Hat: " + (hatActive ? "ON" : "OFF")),
            button -> {
                hatActive = !hatActive;
                button.setMessage(Component.literal("Hat: " + (hatActive ? "ON" : "OFF")));
            }
        ).bounds(leftX, 110, 100, 20).build());

        // 3. Right Column: Cosmetic Toggles
        int rightX = this.width - 130;
        this.addRenderableWidget(Button.builder(
            Component.literal("Wings: " + (wingsActive ? "ON" : "OFF")),
            button -> {
                wingsActive = !wingsActive;
                button.setMessage(Component.literal("Wings: " + (wingsActive ? "ON" : "OFF")));
            }
        ).bounds(rightX, 80, 100, 20).build());

        this.addRenderableWidget(Button.builder(
            Component.literal("Boots: " + (bootsActive ? "ON" : "OFF")),
            button -> {
                bootsActive = !bootsActive;
                button.setMessage(Component.literal("Boots: " + (bootsActive ? "ON" : "OFF")));
            }
        ).bounds(rightX, 110, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Draw the background gradient
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xFF2B79CC, 0xFF0E4C5C);

        // Draw twinkling stars and snow particles
        SnowParticleRenderer.render(guiGraphics, this.width, this.height);

        // Draw screen title
        guiGraphics.drawCenteredString(this.font, Component.literal("XmasLegacy Cosmetics Menu"), this.width / 2, 20, 0xFFFFFFFF);

        // Draw center container box for the character or message
        int containerWidth = 160;
        int containerHeight = 140;
        int containerX = (this.width - containerWidth) / 2;
        int containerY = 60;
        guiGraphics.fill(containerX, containerY, containerX + containerWidth, containerY + containerHeight, 0x600B1A24);
        // Draw a light blue border around the container
        guiGraphics.fill(containerX, containerY, containerX + containerWidth, containerY + 1, 0xFF8EBAEB);
        guiGraphics.fill(containerX, containerY, containerX + 1, containerY + containerHeight, 0xFF8EBAEB);
        guiGraphics.fill(containerX, containerY + containerHeight - 1, containerX + containerWidth, containerY + containerHeight, 0xFF8EBAEB);
        guiGraphics.fill(containerX + containerWidth - 1, containerY, containerX + containerWidth, containerY + containerHeight, 0xFF8EBAEB);

        // Render player or fallback notice
        if (this.minecraft.player != null) {
            // Render 3D player model facing the mouse
            int playerX = this.width / 2;
            int playerY = containerY + 120;
            int scale = 50;
            float lookX = (float) (playerX - mouseX);
            float lookY = (float) (playerY - 50 - mouseY);
            float yaw = (float) Math.atan(lookX / 40.0f);
            float pitch = (float) Math.atan(lookY / 40.0f);
            Quaternionf bodyRotation = new Quaternionf().rotateZ((float) Math.PI).rotateY(yaw * 20.0f * 0.017453292f);
            Quaternionf headRotation = new Quaternionf().rotateX(-pitch * 20.0f * 0.017453292f);
            Vector3f offset = new Vector3f();
            InventoryScreen.renderEntityInInventory(guiGraphics, (float) playerX, (float) playerY, (float) scale, offset, bodyRotation, headRotation, this.minecraft.player);
        } else {
            // Draw placeholder text notice
            int textY = containerY + 45;
            guiGraphics.drawCenteredString(this.font, Component.literal("3D Preview only"), this.width / 2, textY, 0xFFA0A0A0);
            guiGraphics.drawCenteredString(this.font, Component.literal("available in-game."), this.width / 2, textY + 12, 0xFFA0A0A0);
            guiGraphics.drawCenteredString(this.font, Component.literal("Connect to a world"), this.width / 2, textY + 30, 0xFF8EBAEB);
            guiGraphics.drawCenteredString(this.font, Component.literal("to preview cosmetics."), this.width / 2, textY + 42, 0xFF8EBAEB);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
