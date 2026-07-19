package com.xmaslegacy.menu.mixin;

import com.xmaslegacy.menu.ModMenuHelper;
import com.xmaslegacy.menu.config.ModConfig;
import com.xmaslegacy.menu.render.SnowParticleRenderer;
import com.xmaslegacy.menu.screen.ModSettingsScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    @Unique
    private static final ResourceLocation CUSTOM_LOGO = new ResourceLocation("xmaslegacymenu", "textures/gui/logo.png");

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void onInitHead(CallbackInfo ci) {
        SnowParticleRenderer.init(this.width, this.height);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInitReturn(CallbackInfo ci) {
        this.clearWidgets();

        int buttonWidth = 200;
        int buttonHeight = 24;
        int spacing = 28;
        int centerX = (this.width - buttonWidth) / 2;
        int startY = this.height / 2 - 20;

        // Singleplayer
        this.addRenderableWidget(Button.builder(
            Component.literal("\u25C9  Singleplayer"),
            button -> this.minecraft.setScreen(new SelectWorldScreen(this))
        ).bounds(centerX, startY, buttonWidth, buttonHeight).build());

        // Multiplayer
        this.addRenderableWidget(Button.builder(
            Component.literal("\u25EB  Multiplayer"),
            button -> this.minecraft.setScreen(new JoinMultiplayerScreen(this))
        ).bounds(centerX, startY + spacing, buttonWidth, buttonHeight).build());

        // Settings
        this.addRenderableWidget(Button.builder(
            Component.literal("\u2699  Settings"),
            button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))
        ).bounds(centerX, startY + spacing * 2, buttonWidth, buttonHeight).build());

        // Mods
        this.addRenderableWidget(Button.builder(
            Component.literal("\u25A6  Mods"),
            button -> {
                if (ModMenuHelper.isModMenuLoaded()) {
                    ModMenuHelper.openModsScreen(this);
                } else {
                    button.active = false;
                    button.setMessage(Component.literal("\u25A6  No ModMenu"));
                }
            }
        ).bounds(centerX, startY + spacing * 3, buttonWidth, buttonHeight).build());

        // QUIT GAME ??separated at the bottom with extra gap
        this.addRenderableWidget(Button.builder(
            Component.literal("QUIT GAME"),
            button -> this.minecraft.stop()
        ).bounds(centerX, startY + spacing * 4 + 20, buttonWidth, buttonHeight).build());
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        // Dark solid background to mimic Feather's dark theme
        guiGraphics.fill(0, 0, this.width, this.height, 0xFF181818);

        if (ModConfig.snowEnabled) {
            SnowParticleRenderer.render(guiGraphics, this.width, this.height);
        }

        // Feather-like Logo and Text at the top center
        int logoSize = 32;
        int logoX = (this.width - logoSize - 120) / 2; // Offset to left of text
        int logoY = this.height / 4 - 20;
        
        guiGraphics.blit(CUSTOM_LOGO, logoX, logoY, 0, 0, logoSize, logoSize, logoSize, logoSize);
        guiGraphics.drawString(this.font, "XMASLEGACY CLIENT", logoX + logoSize + 10, logoY + (logoSize - 8) / 2, 0xFFFFFFFF, true);

        // Render widgets (buttons)
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Version text at bottom
        guiGraphics.drawCenteredString(this.font, "XmasLegacy 1.21.1 (release/latest)", this.width / 2, this.height - 12, 0x60FFFFFF);

        // Right Shift hint
        guiGraphics.drawString(this.font, "Right Shift \u2192 Settings", 4, this.height - 12, 0x30FFFFFF, false);

        ci.cancel();
    }
}
