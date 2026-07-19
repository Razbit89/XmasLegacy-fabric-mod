package com.xmaslegacy.menu.mixin;

import com.xmaslegacy.menu.ModMenuHelper;
import com.xmaslegacy.menu.render.SnowParticleRenderer;
import com.xmaslegacy.menu.screen.CosmeticsScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInitReturn(CallbackInfo ci) {
        // 1. Clear default Minecraft menu widgets/buttons
        this.clearWidgets();

        // 2. Setup left sidebar tab buttons
        int buttonWidth = 120;
        int buttonHeight = 20;
        int x = (140 - buttonWidth) / 2; // Centered in the 140px sidebar

        // Singleplayer
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.singleplayer"),
            button -> this.minecraft.setScreen(new SelectWorldScreen(this))
        ).bounds(x, 130, buttonWidth, buttonHeight).build());

        // Multiplayer
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.multiplayer"),
            button -> this.minecraft.setScreen(new JoinMultiplayerScreen(this))
        ).bounds(x, 155, buttonWidth, buttonHeight).build());

        // Cosmetics (Custom Screen)
        this.addRenderableWidget(Button.builder(
            Component.literal("Cosmetics"),
            button -> this.minecraft.setScreen(new CosmeticsScreen(this))
        ).bounds(x, 180, buttonWidth, buttonHeight).build());

        // Options
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.options"),
            button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))
        ).bounds(x, 205, buttonWidth, buttonHeight).build());

        // Mods (Safe dynamic link)
        this.addRenderableWidget(Button.builder(
            Component.literal("Mods"),
            button -> {
                if (ModMenuHelper.isModMenuLoaded()) {
                    ModMenuHelper.openModsScreen(this);
                } else {
                    // Fallback visual notification or disable button
                    button.active = false;
                    button.setMessage(Component.literal("No ModMenu"));
                }
            }
        ).bounds(x, 230, buttonWidth, buttonHeight).build());

        // Exit (placed at the bottom of the sidebar)
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.quit"),
            button -> this.minecraft.stop()
        ).bounds(x, this.height - 30, buttonWidth, buttonHeight).build());
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void onInitHead(CallbackInfo ci) {
        // Initialize snow and stars renderer
        SnowParticleRenderer.init(this.width, this.height);
    }

    @Inject(method = "renderPanorama", at = @At("HEAD"), cancellable = true)
    private void onRenderPanorama(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        // 1. Draw the cyan/sky-blue to dark-cyan gradient background
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xFF2B79CC, 0xFF0E4C5C);

        // 2. Render falling snow particles and stars behind widgets
        SnowParticleRenderer.render(guiGraphics, this.width, this.height);

        // 3. Draw the left sidebar background (semi-transparent dark navy)
        guiGraphics.fill(0, 0, 140, this.height, 0x900B1A24);

        // 4. Draw the vertical divider line (sky-blue highlight)
        guiGraphics.fill(139, 0, 140, this.height, 0xFF8EBAEB);

        ci.cancel();
    }
}
