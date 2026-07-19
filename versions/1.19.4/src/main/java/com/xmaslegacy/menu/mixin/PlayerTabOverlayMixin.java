package com.xmaslegacy.menu.mixin;

import com.xmaslegacy.menu.config.ModConfig;

import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

/**
 * Mixin to prepend a snowflake symbol (?? before each player's display name
 * in the Tab player list overlay.
 */
@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void onGetNameForDisplay(PlayerInfo playerInfo, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Component> cir) {
        if (!ModConfig.tabSnowflakeEnabled) {
            return;
        }
        Component original = cir.getReturnValue();
        if (original == null) return;
        
        // Prepend snowflake symbol with light blue color
        MutableComponent snowflake = Component.literal("\u2744 ").withStyle(style ->
            style.withColor(0xAADDFF)
        );
        cir.setReturnValue(snowflake.append(original));
    }
}
