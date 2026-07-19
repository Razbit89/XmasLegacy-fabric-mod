package com.xmaslegacy.menu;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ModMenuHelper {
    public static boolean isModMenuLoaded() {
        return FabricLoader.getInstance().isModLoaded("modmenu");
    }

    public static void openModsScreen(Screen parent) {
        try {
            // Use reflection to instantiate and open ModsScreen dynamically
            // to avoid hard compile-time dependency on ModMenu classes
            Class<?> modsScreenClass = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
            Screen modsScreen = (Screen) modsScreenClass
                    .getConstructor(Screen.class)
                    .newInstance(parent);
            Minecraft.getInstance().setScreen(modsScreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
