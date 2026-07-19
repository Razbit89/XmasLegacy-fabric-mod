package com.xmaslegacy.menu.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.Util;
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
        // Only override rendering if the current screen is TitleScreen and the widget is visible
        if (Minecraft.getInstance().screen instanceof TitleScreen && this.visible) {
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getHeight();
            boolean hovered = isHovered();
            boolean activeWidget = this.active;

            int bgColor;
            int borderColor;
            int textColor = activeWidget ? 0xFFFFFDD0 : 0xFFA0A0A0; // Cozy warm eggshell white text

            String btnText = getMessage().getString();
            boolean isQuit = btnText.contains("종료") || btnText.toLowerCase().contains("quit") || btnText.toLowerCase().contains("exit");

            // Christmas Theme Color Palette:
            // Pine Green: #0C2E1F, Gold outline: #E5C158, Holiday Red hover: #C0392B, Crimson hover: #9E1B1B
            if (isQuit) {
                bgColor = hovered ? 0xFF9E1B1B : 0x700C2E1F;
                borderColor = hovered ? 0xFFFFFFFF : 0x50FFFFFF;
            } else {
                bgColor = hovered ? 0xFFC0392B : 0x700C2E1F;
                borderColor = hovered ? 0xFFFFFFFF : 0x60E5C158;
            }

            // Draw clean premium button background
            guiGraphics.fill(x, y, x + width, y + height, 0x40000000); // dark shadow overlay
            guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, bgColor); // primary fill

            // Draw border outline
            guiGraphics.fill(x, y, x + width, y + 1, borderColor);
            guiGraphics.fill(x, y, x + 1, y + height, borderColor);
            guiGraphics.fill(x, y + height - 1, x + width, y + height, borderColor);
            guiGraphics.fill(x + width - 1, y, x + width, y + height, borderColor);

            // Draw button text
            int textX = x + width / 2;
            int textY = y + (height - 8) / 2;
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), textX, textY, textColor);

            // Subtle "shine/glow" animation effect when hovered
            if (hovered && activeWidget) {
                long time = Util.getMillis() / 150;
                int shineAlpha = (int) (15 + Math.sin(time) * 8);
                int shineColor = (shineAlpha << 24) | 0xFFFFFF;
                guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, shineColor);
            }

            ci.cancel();
        }
    }
}
