package com.xmaslegacy.menu.mixin;

import com.xmaslegacy.menu.render.SnowParticleRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
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

    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        // Initialize snow particles with current screen dimensions
        SnowParticleRenderer.init(this.width, this.height);
    }

    @Inject(method = "renderPanorama", at = @At("HEAD"), cancellable = true)
    private void onRenderPanorama(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        // 1. Draw the cyan/sky-blue to dark-cyan gradient background
        // Colors: #2B79CC (top) and #0E4C5C (bottom)
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0xFF2B79CC, 0xFF0E4C5C);

        // 2. Render falling snow particles behind widgets/logo
        SnowParticleRenderer.render(guiGraphics, this.width, this.height);

        ci.cancel();
    }
}
