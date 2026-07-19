package com.xmaslegacy.menu.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
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
        // Render custom XmasLegacy logo instead of default Minecraft logo
        int logoWidth = 120;
        int logoHeight = 120;
        int x = (screenWidth - logoWidth) / 2;

        // Blit texture (custom logo)
        guiGraphics.blit(CUSTOM_LOGO, x, y, 0, 0, logoWidth, logoHeight, logoWidth, logoHeight);
        ci.cancel();
    }
}
