package com.example.pvptimingoptimizer.hud;

import com.example.pvptimingoptimizer.config.ModConfig;
import com.example.pvptimingoptimizer.features.InputBuffer;
import com.example.pvptimingoptimizer.features.CombatTiming;
import com.example.pvptimingoptimizer.features.PingCompensation;
import com.example.pvptimingoptimizer.features.PredictiveSwap;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

public class DebugHud {
    private final PingCompensation pingCompensation;
    private final PredictiveSwap predictiveSwap;
    private final InputBuffer inputBuffer;
    private final CombatTiming combatTiming;
    private final Identifier id = Identifier.of("accurateshielddisable", "debug_hud");

    public DebugHud(PingCompensation pingCompensation,
                    PredictiveSwap predictiveSwap,
                    InputBuffer inputBuffer,
                    CombatTiming combatTiming) {
        this.pingCompensation = pingCompensation;
        this.predictiveSwap = predictiveSwap;
        this.inputBuffer = inputBuffer;
        this.combatTiming = combatTiming;
    }

    public void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                id,
                this::render
        );
    }

    private void render(GuiGraphics graphics, DeltaTracker tickCounter) {
        ModConfig config = ModConfig.getConfig();
        if (!config.enabled || !config.debugHud) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        int x = 2;
        int y = 2;
        int lineHeight = 10;

        graphics.drawText(client.textRenderer, "PvP Timing Debug", x, y, 0xFFFFFF, false);
        y += lineHeight;

        int ping = pingCompensation.getPing();
        graphics.drawText(client.textRenderer, "Ping: " + ping + "ms", x, y, 0xAAAAAA, false);
        y += lineHeight;

        float tickRate = pingCompensation.getTickRate();
        graphics.drawText(client.textRenderer, "Server Tick Rate: " + String.format("%.1f", tickRate) + " TPS", x, y, 0xAAAAAA, false);
        y += lineHeight;

        int ticks = pingCompensation.getTickOffset();
        String offsetText = ticks >= 0 ? "+" + ticks + " ticks" : "-" + Math.abs(ticks) + " ticks";
        graphics.drawText(client.textRenderer, "Prediction Offset: " + offsetText, x, y, 0xAAAAAA, false);
        y += lineHeight;

        int confidence = predictiveSwap.getConfidence();
        graphics.drawText(client.textRenderer, "Swap Confidence: " + confidence + "%", x, y, 0xAAAAAA, false);
        y += lineHeight;

        String weapon = combatTiming.getWeaponType();
        graphics.drawText(client.textRenderer, "Weapon: " + weapon, x, y, 0xAAAAAA, false);
        y += lineHeight;

        float cd = combatTiming.getCooldownProgress();
        graphics.drawText(client.textRenderer, "Cooldown: " + String.format("%.0f%%", cd * 100), x, y, 0xAAAAAA, false);
        y += lineHeight;

        if (config.showPingData) {
            graphics.drawText(client.textRenderer, "Buffer Size: " + inputBuffer.getSize(), x, y, 0xAAAAAA, false);
        }
    }
}
