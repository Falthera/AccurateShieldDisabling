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
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class PvPClient implements ClientModInitializer {
    private static KeyBinding swapKeybinding;

    private static TickTimer tickTimer;
    private static PredictiveSwap predictiveSwap;
    private static PingCompensation pingCompensation;
    private static InputBuffer inputBuffer;
    private static CombatTiming combatTiming;
    private static DebugHud debugHud;

    public static void init() {
        ConfigManager.loadConfig();

        tickTimer = new TickTimer();
        predictiveSwap = new PredictiveSwap(tickTimer);
        pingCompensation = new PingCompensation();
        inputBuffer = new InputBuffer();
        combatTiming = new CombatTiming();

        swapKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pvptimingoptimizer.weapon_swap",
                GLFW.GLFW_KEY_F,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(PvPClient::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
        if (client == null || client.player == null) {
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

        if (swapKeybinding.consumeClick()) {
            int currentSlot = client.player.getInventory().selectedSlot;
            inputBuffer.recordSwap(currentSlot);
        }

        if (client.options.attackKey.consumeClick()) {
            inputBuffer.recordAttack();
        }

        for (int i = 0; i < 9; i++) {
            if (client.options.hotbarKeys[i].consumeClick()) {
                inputBuffer.recordHotbar(i);
            }
        }

        if (debugHud == null) {
            debugHud = new DebugHud(pingCompensation, predictiveSwap, inputBuffer, combatTiming);
            debugHud.register();
        }
    }
}
