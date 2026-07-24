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
    private int attackWindowEnd = 0;
    private int lastAttackTick = 0;

    private static final int DEFAULT_WINDOW_TICKS = 10;
    private static final int ATTACK_INTERVAL_TICKS = 1;

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
                if (config.autoAttackOnSwap) {
                    pendingAutoAttack = true;
                    attackWindowEnd = timer.getElapsedTicks() + getAttackWindowTicks();
                    lastAttackTick = 0;
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
        if (currentTick >= attackWindowEnd) {
            pendingAutoAttack = false;
            return false;
        }
        if (currentTick < lastAttackTick + ATTACK_INTERVAL_TICKS) {
            return false;
        }
        return true;
    }

    public void onAttackSent() {
        lastAttackTick = timer.getElapsedTicks();
    }

    public void cancelPendingAttack() {
        pendingAutoAttack = false;
    }

    private int getAttackWindowTicks() {
        ModConfig config = ModConfig.getConfig();
        return Math.max(1, config.attackWindowTicks);
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

    public int getRemainingWindowTicks(int currentTick) {
        if (!pendingAutoAttack) {
            return 0;
        }
        return Math.max(0, attackWindowEnd - currentTick);
    }
}
