package com.xmaslegacy.menu.mixin;

import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {
    @Shadow public abstract int getX();
    @Shadow public abstract int getY();
    @Shadow public abstract int getWidth();
    @Shadow public abstract int getHeight();
    @Shadow public abstract Component getMessage();
    @Shadow public abstract boolean isHovered();
    @Shadow public boolean active;
    @Shadow public boolean visible;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof TitleScreen && this.visible) {
            int x = getX();
            int y = getY();
            int w = getWidth();
            int h = getHeight();
            boolean hovered = isHovered();

            String btnText = getMessage().getString();
            boolean isQuit = btnText.contains("\uC885\uB8CC") || btnText.toLowerCase().contains("quit");

            int bgColor;
            int textColor;

            if (isQuit) {
                // QUIT GAME: Red background, white text
                bgColor = hovered ? 0xE0E74C3C : 0xE0C0392B;
                textColor = 0xFFFFFFFF;
            } else {
                // Normal buttons: Dark gray, lighter on hover
                bgColor = hovered ? 0xE0333333 : 0xE01C1C1C;
                textColor = active ? 0xFFFFFFFF : 0xFF555555;
            }

            // Background fill (No borders)
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x, y, x + w, y + h, bgColor);

            // Centered text
            net.minecraft.client.gui.GuiComponent.drawCenteredString(poseStack, 
                Minecraft.getInstance().font, getMessage(),
                x + w / 2, y + (h - 8) / 2, textColor
            );

            ci.cancel();
        }
    }
}
