package com.example.pvptimingoptimizer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.pvptimingoptimizer.client.PvPClient;
import net.minecraft.client.MinecraftClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = MinecraftClient.getInstance()
            .runDirectory.toPath().resolve("config").resolve("accurateshielddisable.json");
    private static ModConfig config;

    public static ModConfig getConfig() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    public static void loadConfig() {
        config = new ModConfig();
        if (Files.exists(CONFIG_PATH)) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Files.newInputStream(CONFIG_PATH), StandardCharsets.UTF_8))) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    config = loaded;
                }
            } catch (IOException exception) {
                PvPClient.LOGGER.error("Failed to load PvP Timing Optimizer config", exception);
            }
        }
    }

    public static void saveConfig() {
        if (config == null) {
            return;
        }
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    Files.newOutputStream(CONFIG_PATH), StandardCharsets.UTF_8)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException exception) {
            PvPClient.LOGGER.error("Failed to save PvP Timing Optimizer config", exception);
        }
    }
}
