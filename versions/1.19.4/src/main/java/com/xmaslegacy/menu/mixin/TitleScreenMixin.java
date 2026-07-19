package com.xmaslegacy.menu.mixin;

import com.xmaslegacy.menu.ModMenuHelper;
import com.xmaslegacy.menu.config.ModConfig;
import com.xmaslegacy.menu.render.SnowParticleRenderer;
import com.xmaslegacy.menu.screen.ModSettingsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
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
        int buttonHeight = 20;
        int spacing = 26;
        int centerX = (this.width - buttonWidth) / 2;
        int startY = this.height / 2 - 10;

        // Singleplayer
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.singleplayer"),
            button -> this.minecraft.setScreen(new SelectWorldScreen(this))
        ).bounds(centerX, startY, buttonWidth, buttonHeight).build());

        // Multiplayer
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.multiplayer"),
            button -> this.minecraft.setScreen(new JoinMultiplayerScreen(this))
        ).bounds(centerX, startY + spacing, buttonWidth, buttonHeight).build());

        // Options
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.options"),
            button -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))
        ).bounds(centerX, startY + spacing * 2, buttonWidth, buttonHeight).build());

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
        ).bounds(centerX, startY + spacing * 3, buttonWidth, buttonHeight).build());

        // QUIT GAME
        this.addRenderableWidget(Button.builder(
            Component.translatable("menu.quit"),
            button -> this.minecraft.stop()
        ).bounds(centerX, startY + spacing * 4 + 16, buttonWidth, buttonHeight).build());
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        this.fillGradient(poseStack, 0, 0, this.width, this.height, 0xFF0A0F14, 0xFF0C1A12);

        if (ModConfig.snowEnabled) {
            SnowParticleRenderer.render(poseStack, this.width, this.height);
        }

        int logoSize = 40;
        int logoX = (this.width - logoSize) / 2;
        int logoY = this.height / 2 - 76;
        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, CUSTOM_LOGO);
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        net.minecraft.client.gui.GuiComponent.blit(poseStack, logoX, logoY, 0, 0, logoSize, logoSize, logoSize, logoSize);

        int textY = logoY + logoSize + 6;
        net.minecraft.client.gui.GuiComponent.drawCenteredString(poseStack, this.font, "XMASLEGACY", this.width / 2, textY, 0xFFE5C158);

        super.render(poseStack, mouseX, mouseY, partialTick);

        net.minecraft.client.gui.GuiComponent.drawCenteredString(poseStack, this.font, "XmasLegacy v1.0.0", this.width / 2, this.height - 12, 0x60FFFFFF);
        net.minecraft.client.gui.GuiComponent.drawString(poseStack, this.font, "Right Shift \u2192 Settings", 4, this.height - 12, 0x30FFFFFF);

        ci.cancel();
    }

    @Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.minecraft.setScreen(new ModSettingsScreen(this));
            cir.setReturnValue(true);
        }
    }
}
