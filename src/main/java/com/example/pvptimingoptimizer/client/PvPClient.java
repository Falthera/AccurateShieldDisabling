package com.example.pvptimingoptimizer.client;

import com.example.pvptimingoptimizer.Main;
import com.example.pvptimingoptimizer.config.ConfigManager;
import com.example.pvptimingoptimizer.config.ModConfig;
import com.example.pvptimingoptimizer.features.CombatTiming;
import com.example.pvptimingoptimizer.features.InputBuffer;
import com.example.pvptimingoptimizer.features.PingCompensation;
import com.example.pvptimingoptimizer.features.PredictiveSwap;
import com.example.pvptimingoptimizer.hud.DebugHud;
import com.example.pvptimingoptimizer.util.TickTimer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvPClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.MOD_ID);
    private static KeyBinding swapKeybinding;

    private static TickTimer tickTimer;
    private static PredictiveSwap predictiveSwap;
    private static PingCompensation pingCompensation;
    private static InputBuffer inputBuffer;
    private static CombatTiming combatTiming;
    private static DebugHud debugHud;

    private static int pendingAttackTicks = 0;
    private static final int MAX_PENDING_TICKS = 20;

    @Override
    public void onInitializeClient() {
        init();
    }

    public static void init() {
        long start = System.nanoTime();
        LOGGER.info("Accurate Shield Disable client init starting");

        try {
            ConfigManager.loadConfig();

            tickTimer = new TickTimer();
            predictiveSwap = new PredictiveSwap(tickTimer);
            pingCompensation = new PingCompensation();
            inputBuffer = new InputBuffer();
            combatTiming = new CombatTiming();

            swapKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.accurateshielddisable.weapon_swap",
                    GLFW.GLFW_KEY_F,
                    KeyBinding.Category.MISC
            ));

            ClientTickEvents.END_CLIENT_TICK.register(PvPClient::onClientTick);
            ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
                try {
                    debugHud = new DebugHud(pingCompensation, predictiveSwap, inputBuffer, combatTiming);
                    debugHud.register();
                    LOGGER.info("Registered DebugHud");
                } catch (Throwable t) {
                    LOGGER.error("Failed to register DebugHud", t);
                }
            });

            long ms = (System.nanoTime() - start) / 1_000_000L;
            LOGGER.info("Accurate Shield Disable client init complete ({} ms)", ms);
        } catch (Throwable t) {
            LOGGER.error("Failed during Accurate Shield Disable client init", t);
            throw t;
        }
    }

    private static void onClientTick(MinecraftClient client) {
        try {
            if (client == null || client.player == null || client.world == null) {
                return;
            }

            ModConfig config = ModConfig.getConfig();
            if (!config.enabled) {
                return;
            }

            tickTimer.tick();
            predictiveSwap.tick();
            pingCompensation.update();
            inputBuffer.tick();
            combatTiming.tick();

            if (swapKeybinding.wasPressed()) {
                int currentSlot = client.player.getInventory().getSelectedSlot();
                inputBuffer.recordSwap(currentSlot);
            }

            if (client.options.attackKey.wasPressed()) {
                inputBuffer.recordAttack();
            }

            for (int i = 0; i < 9; i++) {
                if (client.options.hotbarKeys[i].wasPressed()) {
                    inputBuffer.recordHotbar(i);
                }
            }

            if (pendingAttackTicks > 0) {
                pendingAttackTicks--;
            }

            if (config.autoAttackOnSwap && predictiveSwap.shouldAttackNow(tickTimer.getElapsedTicks())) {
                if (combatTiming.isCombatWeapon() && client.player != null) {
                    if (client.crosshairTarget instanceof EntityHitResult entityHit) {
                        attemptAttack(client, entityHit.getEntity());
                        predictiveSwap.onAttackSent();
                    }
                }
            }

            if (config.pingCompensationEnabled && pingCompensation.getPing() > 0) {
                handlePingCompensation(client, config);
            }

            if (config.inputBufferEnabled && inputBuffer.wasSwapFollowedByAttack()) {
                handleInputBuffer(client);
            }
        } catch (Throwable t) {
            LOGGER.error("Error in onClientTick", t);
        }
    }

    private static void attemptAttack(MinecraftClient client, net.minecraft.entity.Entity target) {
        ModConfig config = ModConfig.getConfig();
        int attempts = getAttemptsFromConfig(config.predictionStrength);

        for (int i = 0; i < attempts; i++) {
            resetAttackCooldown();
            pendingAttackTicks = Math.max(pendingAttackTicks, 2);

            try {
                client.interactionManager.attackEntity(client.player, target);
            } catch (Throwable t) {
                LOGGER.error("Attack attempt failed", t);
            }

            if (config.pingCompensationEnabled) {
                int pingTicks = (int) Math.ceil(pingCompensation.getPing() / 1000.0 * 20.0);
                for (int j = 0; j < pingTicks && i + 1 < attempts; j++) {
                    tickTimer.tick();
                }
            }
        }
    }

    private static int getAttemptsFromConfig(ModConfig.PredictionStrength strength) {
        return switch (strength) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
        };
    }

    private static void handlePingCompensation(MinecraftClient client, ModConfig config) {
        int ping = pingCompensation.getPing();
        if (ping <= 0) {
            return;
        }

        int offsetTicks = (int) Math.round((ping / 1000.0) * 20.0 * (config.latencyMultiplier / 100.0));

        for (int i = 0; i < offsetTicks; i++) {
            tickTimer.tick();
        }
    }

    private static void handleInputBuffer(MinecraftClient client) {
        if (client.crosshairTarget instanceof EntityHitResult entityHit) {
            resetAttackCooldown();
            pendingAttackTicks = 2;

            try {
                client.interactionManager.attackEntity(client.player, entityHit.getEntity());
            } catch (Throwable t) {
                LOGGER.error("Input buffer attack failed", t);
            }
        }
    }
}
