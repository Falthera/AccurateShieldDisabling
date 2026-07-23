package com.example.pvptimingoptimizer.features;

import com.example.pvptimingoptimizer.config.ModConfig;
import com.example.pvptimingoptimizer.util.TickTimer;
import com.example.pvptimingoptimizer.util.NetworkUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.*;

public class PredictiveSwap {
    private int previousSlot = -1;
    private int currentSlot;
    private boolean swapDetected = false;
    private int swapCooldown = 0;
    private final TickTimer timer;

    private boolean pendingAutoAttack = false;
    private int scheduledAttackTick = 0;

    public PredictiveSwap(TickTimer timer) {
        this.timer = timer;
    }

    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        ClientPlayerEntity player = client.player;
        currentSlot = player.getInventory().getSelectedSlot();

        if (previousSlot != -1 && previousSlot != currentSlot) {
            swapDetected = true;
            swapCooldown = ModConfig.getConfig().bufferLength;

            if (isCombatSwap()) {
                ModConfig config = ModConfig.getConfig();
                int predictedTickOffset = config.swapOffset + (int) NetworkUtils.ticksFromPing(NetworkUtils.getPing());
                int strength = getStrengthFromConfig(config.predictionStrength);
                if (predictedTickOffset > 0 && strength > 0) {
                    pendingAutoAttack = true;
                    scheduledAttackTick = timer.getElapsedTicks() + Math.max(1, predictedTickOffset);
                }
            }
        } else if (swapCooldown > 0) {
            swapCooldown--;
        }

        if (swapDetected && swapCooldown <= 0) {
            swapDetected = false;
        }

        if (!isHoldingCombatWeapon()) {
            pendingAutoAttack = false;
        }

        previousSlot = currentSlot;
    }

    public boolean shouldAttackNow(int currentTick) {
        if (!pendingAutoAttack) {
            return false;
        }
        if (swapCooldown > 0) {
            return false;
        }
        if (currentTick < scheduledAttackTick) {
            return false;
        }
        return true;
    }

    public void cancelPendingAttack() {
        pendingAutoAttack = false;
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
        ItemStack stack = client.player.getMainHandStack();
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return item instanceof AxeItem;
    }

    private boolean isHoldingCombatWeapon() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return false;
        }
        ItemStack stack = client.player.getMainHandStack();
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return item instanceof AxeItem;
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
