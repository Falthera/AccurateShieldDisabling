package com.example.pvptimingoptimizer;

import com.example.pvptimingoptimizer.client.PvPClient;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ClientModInitializer {
    public static final String MOD_ID = "accurateshielddisable";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Accurate Shield Disable client init starting");
        PvPClient.init();
        LOGGER.info("Accurate Shield Disable client init complete");
    }
}
