package com.xmaslegacy.menu.mixin;

import com.xmaslegacy.menu.screen.ModSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof TitleScreen && keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            Minecraft.getInstance().setScreen(new ModSettingsScreen((Screen) (Object) this));
            cir.setReturnValue(true);
        }
    }
}
