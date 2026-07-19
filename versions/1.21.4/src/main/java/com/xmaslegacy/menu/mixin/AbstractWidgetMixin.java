package com.xmaslegacy.menu.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
    private void onRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof TitleScreen && this.visible) {
            int x = getX();
            int y = getY();
            int w = getWidth();
            int h = getHeight();
            boolean hovered = isHovered();

            String btnText = getMessage().getString();
            boolean isQuit = btnText.contains("\uC885\uB8CC") || btnText.toLowerCase().contains("quit");

            int bgColor;
            int borderColor;
            int textColor;

            if (isQuit) {
                // QUIT GAME: transparent dark bg, red text, red bg on hover
                bgColor = hovered ? 0x60C0392B : 0x30000000;
                borderColor = hovered ? 0x40C0392B : 0x10FFFFFF;
                textColor = active ? (hovered ? 0xFFFFFFFF : 0xFFE74C3C) : 0xFF555555;
            } else {
                // Normal buttons: subtle dark glass, brighter on hover
                bgColor = hovered ? 0x50FFFFFF : 0x30000000;
                borderColor = hovered ? 0x30FFFFFF : 0x10FFFFFF;
                textColor = active ? (hovered ? 0xFFFFFFFF : 0xFFD0D0D0) : 0xFF555555;
            }

            // Background fill
            guiGraphics.fill(x, y, x + w, y + h, bgColor);

            // 1px subtle border
            guiGraphics.fill(x, y, x + w, y + 1, borderColor);
            guiGraphics.fill(x, y + h - 1, x + w, y + h, borderColor);
            guiGraphics.fill(x, y, x + 1, y + h, borderColor);
            guiGraphics.fill(x + w - 1, y, x + w, y + h, borderColor);

            // Centered text
            guiGraphics.drawCenteredString(
                Minecraft.getInstance().font, getMessage(),
                x + w / 2, y + (h - 8) / 2, textColor
            );

            ci.cancel();
        }
    }
}
