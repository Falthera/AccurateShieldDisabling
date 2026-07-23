package com.example.pvptimingoptimizer.features;

import com.example.pvptimingoptimizer.config.ModConfig;
import com.example.pvptimingoptimizer.util.TickTimer;
import com.example.pvptimingoptimizer.util.NetworkUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.item.*;
import net.minecraft.screen.slot.SlotActionType;

public class PredictiveSwap {
    private int previousSlot = -1;
    private int currentSlot;
    private boolean swapDetected = false;
    private int swapCooldown = 0;
    private final TickTimer timer;

    public PredictiveSwap(TickTimer timer) {
        this.timer = timer;
    }

    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        LocalPlayer player = client.player;
        currentSlot = player.getInventory().selectedSlot;

        if (previousSlot != -1 && previousSlot != currentSlot) {
            swapDetected = true;
            swapCooldown = ModConfig.getConfig().bufferLength;

            if (isCombatSwap()) {
                ModConfig config = ModConfig.getConfig();
                int predictedTickOffset = config.swapOffset + (int) NetworkUtils.ticksFromPing(NetworkUtils.getPing());
                int strength = getStrengthFromConfig(config.predictionStrength);
                if (predictedTickOffset > 0 && strength > 0) {
                }
            }
        } else if (swapCooldown > 0) {
            swapCooldown--;
        }

        if (swapDetected && swapCooldown <= 0) {
            swapDetected = false;
        }

        previousSlot = currentSlot;
    }

    private int getStrengthFromConfig(ModConfig.PredictionStrength strength) {
        return switch (strength) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
        };
    }

    private boolean isCombatSwap() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return false;
        }
        ItemStack main = client.player.getMainHandItem();
        if (main.isEmpty()) {
            return false;
        }
        Item item = main.getItem();
        return item instanceof SwordItem || item instanceof AxeItem;
    }

    public int getPreviousSlot() {
        return previousSlot;
    }

    public int getCurrentSlot() {
        return currentSlot;
    }

    public boolean wasSwapDetected() {
        return swapDetected;
    }

    public int getConfidence() {
        if (!swapDetected) {
            return 0;
        }
        return 50 + (swapCooldown * 10);
    }
}
