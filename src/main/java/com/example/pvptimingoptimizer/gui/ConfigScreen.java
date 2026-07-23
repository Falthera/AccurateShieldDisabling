package com.example.pvptimingoptimizer.gui;

import com.example.pvptimingoptimizer.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import me.shedaniel.cloth.config2.api.ConfigBuilder;
import me.shedaniel.cloth.config2.api.ConfigEntryBuilder;

public class ConfigScreen {
    public static Screen create(Screen parent) {
        ModConfig config = ModConfig.getConfig();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.pvptimingoptimizer.title"))
                .setSavingRunnable(config::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        builder.getOrCreateCategory(Text.translatable("category.pvptimingoptimizer.general"))
                .addEntry(entryBuilder.startBooleanToggle(
                                Text.translatable("option.pvptimingoptimizer.enabled"), config.enabled)
                        .setDefaultValue(true)
                        .setSaveConsumer(val -> config.enabled = val)
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(
                                Text.translatable("option.pvptimingoptimizer.debug_hud"), config.debugHud)
                        .setDefaultValue(false)
                        .setSaveConsumer(val -> config.debugHud = val)
                        .build());

        builder.getOrCreateCategory(Text.translatable("category.pvptimingoptimizer.predictive_swap"))
                .addEntry(entryBuilder.startBooleanToggle(
                                Text.translatable("option.pvptimingoptimizer.predictive_swap_enabled"), config.predictiveSwapEnabled)
                        .setDefaultValue(true)
                        .setSaveConsumer(val -> config.predictiveSwapEnabled = val)
                        .build())
                .addEntry(entryBuilder.startEnumSelector(
                                Text.translatable("option.pvptimingoptimizer.prediction_strength"), ModConfig.PredictionStrength.class, config.predictionStrength)
                        .setDefaultValue(ModConfig.PredictionStrength.MEDIUM)
                        .setSaveConsumer(val -> config.predictionStrength = val)
                        .build())
                .addEntry(entryBuilder.startIntSlider(
                                Text.translatable("option.pvptimingoptimizer.swap_offset"), config.swapOffset, -5, 5)
                        .setDefaultValue(0)
                        .setSaveConsumer(val -> config.swapOffset = val)
                        .build());

        builder.getOrCreateCategory(Text.translatable("category.pvptimingoptimizer.ping_compensation"))
                .addEntry(entryBuilder.startBooleanToggle(
                                Text.translatable("option.pvptimingoptimizer.ping_compensation_enabled"), config.pingCompensationEnabled)
                        .setDefaultValue(true)
                        .setSaveConsumer(val -> config.pingCompensationEnabled = val)
                        .build())
                .addEntry(entryBuilder.startIntSlider(
                                Text.translatable("option.pvptimingoptimizer.latency_multiplier"), config.latencyMultiplier, 50, 200)
                        .setDefaultValue(100)
                        .setSaveConsumer(val -> config.latencyMultiplier = val)
                        .build())
                .addEntry(entryBuilder.startBooleanToggle(
                                Text.translatable("option.pvptimingoptimizer.show_ping_data"), config.showPingData)
                        .setDefaultValue(false)
                        .setSaveConsumer(val -> config.showPingData = val)
                        .build());

        builder.getOrCreateCategory(Text.translatable("category.pvptimingoptimizer.input_buffer"))
                .addEntry(entryBuilder.startBooleanToggle(
                                Text.translatable("option.pvptimingoptimizer.input_buffer_enabled"), config.inputBufferEnabled)
                        .setDefaultValue(true)
                        .setSaveConsumer(val -> config.inputBufferEnabled = val)
                        .build())
                .addEntry(entryBuilder.startIntSlider(
                                Text.translatable("option.pvptimingoptimizer.buffer_length"), config.bufferLength, 1, 10)
                        .setDefaultValue(3)
                        .setSaveConsumer(val -> config.bufferLength = val)
                        .build());

        return builder.build();
    }
}
