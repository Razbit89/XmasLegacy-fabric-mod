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

            // Spec colors:
            // 버튼 기본: #5196DF (0xFF5196DF)
            // 버튼 hover: #8EBAEB (0xFF8EBAEB)
            // 텍스트: #EAF6F6 (0xFFEAF6F6)
            int bgColor = hovered ? 0xFF8EBAEB : 0xFF5196DF;
            int borderColor = hovered ? 0xFFFFFFFF : 0xFF8EBAEB;
            int textColor = activeWidget ? 0xFFEAF6F6 : 0xFFA0A0A0;

            // Draw clean premium button background
            guiGraphics.fill(x, y, x + width, y + height, 0x50000000); // semi-transparent background overlay
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
                int shineAlpha = (int) (20 + Math.sin(time) * 10);
                int shineColor = (shineAlpha << 24) | 0xFFFFFF;
                guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, shineColor);
            }

            ci.cancel();
        }
    }
}
