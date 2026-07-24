package com.example.pvptimingoptimizer.hud;

import com.example.pvptimingoptimizer.config.ModConfig;
import com.example.pvptimingoptimizer.features.InputBuffer;
import com.example.pvptimingoptimizer.features.CombatTiming;
import com.example.pvptimingoptimizer.features.PingCompensation;
import com.example.pvptimingoptimizer.features.PredictiveSwap;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
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

    private void render(DrawContext graphics, RenderTickCounter tickCounter) {
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

        graphics.drawText(client.textRenderer, "Accurate Shield Disable", x, y, 0xFFFFFF, false);
        y += lineHeight;

        String weapon = combatTiming.getWeaponType();
        graphics.drawText(client.textRenderer, "Weapon: " + weapon, x, y, 0xAAAAAA, false);
        y += lineHeight;

        float cd = combatTiming.getCooldownProgress();
        graphics.drawText(client.textRenderer, "Cooldown: " + String.format("%.0f%%", cd * 100), x, y, 0xAAAAAA, false);
        y += lineHeight;

        int ping = pingCompensation.getPing();
        graphics.drawText(client.textRenderer, "Ping: " + ping + "ms", x, y, 0xAAAAAA, false);
        y += lineHeight;

        int remaining = predictiveSwap.getRemainingWindowTicks((int) (client.world.getTime() % Integer.MAX_VALUE));
        String windowText = predictiveSwap.wasSwapDetected() ? remaining + " ticks left" : "idle";
        graphics.drawText(client.textRenderer, "Attack Window: " + windowText, x, y, 0xAAAAAA, false);
        y += lineHeight;

        if (config.showPingData) {
            graphics.drawText(client.textRenderer, "Buffer Size: " + inputBuffer.getSize(), x, y, 0xAAAAAA, false);
        }
    }
}
