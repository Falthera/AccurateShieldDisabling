package com.example.pvptimingoptimizer.client;

import com.example.pvptimingoptimizer.Main;
import com.example.pvptimingoptimizer.config.ConfigManager;
import com.example.pvptimingoptimizer.config.ModConfig;
import com.example.pvptimingoptimizer.features.CombatTiming;
import com.example.pvptimingoptimizer.features.InputBuffer;
import com.example.pvptimingoptimizer.features.PingCompensation;
import com.example.pvptimingoptimizer.features.PredictiveSwap;
import com.example.pvptimingoptimizer.hud.DebugHud;
import com.example.pvptimingoptimizer.mixin.ShieldDisableBypass;
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

            if (config.autoAttackOnSwap && predictiveSwap.shouldAttackNow(tickTimer.getElapsedTicks())) {
                if (combatTiming.isCombatWeapon() && client.player != null) {
                    if (client.crosshairTarget instanceof EntityHitResult entityHit) {
                        try {
                            ShieldDisableBypass.setBypass(true);
                            resetAttackCooldown();
                            client.interactionManager.attackEntity(client.player, entityHit.getEntity());
                        } finally {
                            ShieldDisableBypass.setBypass(false);
                        }
                        predictiveSwap.onAttackSent();
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Error in onClientTick", t);
        }
    }

    private static void resetAttackCooldown() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.interactionManager == null) {
            return;
        }

        try {
            java.lang.reflect.Field field = client.interactionManager.getClass().getDeclaredField("attackCooldown");
            field.setAccessible(true);
            field.setInt(client.interactionManager, 0);
        } catch (NoSuchFieldException e) {
            try {
                java.lang.reflect.Field field = client.interactionManager.getClass().getDeclaredField("field_18725");
                field.setAccessible(true);
                field.setInt(client.interactionManager, 0);
            } catch (Exception e2) {
                Class<?> clazz = client.interactionManager.getClass();
                while (clazz != null) {
                    try {
                        java.lang.reflect.Field field = clazz.getDeclaredField("attackCooldown");
                        field.setAccessible(true);
                        field.setInt(client.interactionManager, 0);
                        return;
                    } catch (NoSuchFieldException e3) {
                        clazz = clazz.getSuperclass();
                    } catch (Exception e3) {
                        return;
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
