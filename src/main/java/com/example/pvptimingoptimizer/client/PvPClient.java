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

    public static void init() {
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
    }

    @Override
    public void onInitializeClient() {
        init();
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

        if (debugHud == null) {
            debugHud = new DebugHud(pingCompensation, predictiveSwap, inputBuffer, combatTiming);
            debugHud.register();
        }
    }
}
