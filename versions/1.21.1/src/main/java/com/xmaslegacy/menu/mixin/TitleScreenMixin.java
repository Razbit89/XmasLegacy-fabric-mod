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
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    @Unique
    private static final ResourceLocation CUSTOM_LOGO = ResourceLocation.fromNamespaceAndPath("xmaslegacymenu", "textures/gui/logo.png");

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInitReturn(CallbackInfo ci) {
        // 1. Clear default Minecraft menu widgets/buttons
        this.clearWidgets();

        // 2. Setup centered Feather Client style menu buttons
        int buttonWidth = 160;
        int buttonHeight = 20;
        int x = (this.width - buttonWidth) / 2; // Centered horizontally
        int startY = this.height / 2 - 20; // Vertically centered list start Y

        // Singleplayer
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.singleplayer"),
            button -> this.minecraft.setScreen(new SelectWorldScreen(this))
        ).bounds(x, startY, buttonWidth, buttonHeight).build());

        // Multiplayer
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.multiplayer"),
            button -> this.minecraft.setScreen(new JoinMultiplayerScreen(this))
        ).bounds(x, startY + 26, buttonWidth, buttonHeight).build());

        // Cosmetics
        this.addRenderableWidget(Button.builder(
            Component.literal("Cosmetics"),
            button -> this.minecraft.setScreen(new CosmeticsScreen(this))
        ).bounds(x, startY + 52, buttonWidth, buttonHeight).build());

        // Options
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.options"),
            button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))
        ).bounds(x, startY + 78, buttonWidth, buttonHeight).build());

        // Mods
        this.addRenderableWidget(Button.builder(
            Component.literal("Mods"),
            button -> {
                if (ModMenuHelper.isModMenuLoaded()) {
                    ModMenuHelper.openModsScreen(this);
                } else {
                    button.active = false;
                    button.setMessage(Component.literal("No ModMenu"));
                }
            }
        ).bounds(x, startY + 104, buttonWidth, buttonHeight).build());

        // Exit (Quit Game)
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.quit"),
            button -> this.minecraft.stop()
        ).bounds(x, startY + 130, buttonWidth, buttonHeight).build());
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void onInitHead(CallbackInfo ci) {
        // Initialize snow and stars renderer
        SnowParticleRenderer.init(this.width, this.height);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        // 1. Draw the custom gradient background
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xFF2B79CC, 0xFF0E4C5C);

        // 2. Render falling snow particles and stars
        SnowParticleRenderer.render(guiGraphics, this.width, this.height);

        // 3. Render centered custom logo
        int logoSize = 64;
        int logoX = (this.width - logoSize) / 2;
        int logoY = this.height / 2 - 100;
        guiGraphics.blit(CUSTOM_LOGO, logoX, logoY, 0, 0, logoSize, logoSize, logoSize, logoSize);

        // 4. Draw centered logo text below icon
        int textY = logoY + logoSize + 8;
        guiGraphics.drawCenteredString(this.font, "XMASLEGACY CLIENT", this.width / 2, textY, 0xFFE5C158);

        // 5. Render custom active widgets (buttons) using super.render (skips vanilla logo/splash/version texts)
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        ci.cancel();
    }
}
