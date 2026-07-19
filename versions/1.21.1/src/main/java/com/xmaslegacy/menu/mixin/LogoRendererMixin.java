package com.xmaslegacy.menu.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoRenderer.class)
public class LogoRendererMixin {
    @Unique
    private static final ResourceLocation CUSTOM_LOGO = ResourceLocation.fromNamespaceAndPath("xmaslegacymenu", "textures/gui/logo.png");

    @Inject(method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V", at = @At("HEAD"), cancellable = true)
    private void onRenderLogo(GuiGraphics guiGraphics, int screenWidth, float alpha, int y, CallbackInfo ci) {
        // If we are rendering on the TitleScreen, center the logo inside the left sidebar instead
        if (net.minecraft.client.Minecraft.getInstance().screen instanceof TitleScreen) {
            int logoWidth = 100;
            int logoHeight = 100;
            int x = (140 - logoWidth) / 2; // Centered in the 140px sidebar
            int customY = 15; // Positioned near the top

            guiGraphics.blit(CUSTOM_LOGO, x, customY, 0, 0, logoWidth, logoHeight, logoWidth, logoHeight);
            ci.cancel();
        }
    }
}
