package com.xmaslegacy.menu;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmasLegacyMenuMod implements ClientModInitializer {
    public static final String MOD_ID = "xmaslegacymenu";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing XmasLegacy Custom Menu Mod...");
    }
}
