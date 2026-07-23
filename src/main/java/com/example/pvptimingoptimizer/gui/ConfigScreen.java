package com.example.pvptimingoptimizer.gui;

import com.example.pvptimingoptimizer.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Component;
import me.shedaniel.cloth.config2.api.ConfigBuilder;
import me.shedaniel.cloth.config2.api.ConfigEntryBuilder;

public class ConfigScreen {
    public static Screen create(Screen parent) {
        ModConfig config = ModConfig.getConfig();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.accurateshielddisable.title"))
                .setSavingRunnable(config::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        builder.getOrCreateCategory(Component.translatable("category.accurateshielddisable.general"))
                .addEntry(entryBuilder.startBooleanToggle(
                                Component.translatable("option.accurateshielddisable.enabled"), config.enabled)
                        .setDefaultValue(true)
                        .setSaveConsumer(val -> config.enabled = val)
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(
                                Component.translatable("option.accurateshielddisable.debug_hud"), config.debugHud)
                        .setDefaultValue(false)
                        .setSaveConsumer(val -> config.debugHud = val)
                        .build());

        builder.getOrCreateCategory(Component.translatable("category.accurateshielddisable.predictive_swap"))
                .addEntry(entryBuilder.startBooleanToggle(
                                Component.translatable("option.accurateshielddisable.predictive_swap_enabled"), config.predictiveSwapEnabled)
                        .setDefaultValue(true)
                        .setSaveConsumer(val -> config.predictiveSwapEnabled = val)
                        .build())
                .addEntry(entryBuilder.startEnumSelector(
                                Component.translatable("option.accurateshielddisable.prediction_strength"), ModConfig.PredictionStrength.class, config.predictionStrength)
                        .setDefaultValue(ModConfig.PredictionStrength.MEDIUM)
                        .setSaveConsumer(val -> config.predictionStrength = val)
                        .build())
                .addEntry(entryBuilder.startIntSlider(
                                Component.translatable("option.accurateshielddisable.swap_offset"), config.swapOffset, -5, 5)
                        .setDefaultValue(0)
                        .setSaveConsumer(val -> config.swapOffset = val)
                        .build());

        builder.getOrCreateCategory(Component.translatable("category.accurateshielddisable.ping_compensation"))
                .addEntry(entryBuilder.startBooleanToggle(
                                Component.translatable("option.accurateshielddisable.ping_compensation_enabled"), config.pingCompensationEnabled)
                        .setDefaultValue(true)
                        .setSaveConsumer(val -> config.pingCompensationEnabled = val)
                        .build())
                .addEntry(entryBuilder.startIntSlider(
                                Component.translatable("option.accurateshielddisable.latency_multiplier"), config.latencyMultiplier, 50, 200)
                        .setDefaultValue(100)
                        .setSaveConsumer(val -> config.latencyMultiplier = val)
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(
                                Component.translatable("option.accurateshielddisable.show_ping_data"), config.showPingData)
                        .setDefaultValue(false)
                        .setSaveConsumer(val -> config.showPingData = val)
                        .build());

        builder.getOrCreateCategory(Component.translatable("category.accurateshielddisable.input_buffer"))
                .addEntry(entryBuilder.startBooleanToggle(
                                Component.translatable("option.accurateshielddisable.input_buffer_enabled"), config.inputBufferEnabled)
                        .setDefaultValue(true)
                        .setSaveConsumer(val -> config.inputBufferEnabled = val)
                        .build())
                .addEntry(entryBuilder.startIntSlider(
                                Component.translatable("option.accurateshielddisable.buffer_length"), config.bufferLength, 1, 10)
                        .setDefaultValue(3)
                        .setSaveConsumer(val -> config.bufferLength = val)
                        .build());

        return builder.build();
    }
}
