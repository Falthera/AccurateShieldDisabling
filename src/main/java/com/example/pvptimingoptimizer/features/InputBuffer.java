package com.example.pvptimingoptimizer.features;

import com.example.pvptimingoptimizer.config.ModConfig;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayDeque;
import java.util.Deque;

public class InputBuffer {
    private static class BufferedInput {
        final String type;
        final int tick;
        final int slot;

        BufferedInput(String type, int tick, int slot) {
            this.type = type;
            this.tick = tick;
            this.slot = slot;
        }
    }

    private final Deque<BufferedInput> inputs = new ArrayDeque<>();
    private int globalTick;

    public void tick() {
        globalTick++;
        purge();
    }

    public void recordAttack() {
        if (!ModConfig.getConfig().inputBufferEnabled) {
            return;
        }
        record("attack", getCurrentSlot());
    }

    public void recordSwap(int slot) {
        if (!ModConfig.getConfig().inputBufferEnabled) {
            return;
        }
        record("swap", slot);
    }

    public void recordHotbar(int slot) {
        if (!ModConfig.getConfig().inputBufferEnabled) {
            return;
        }
        record("hotbar", slot);
    }

    public boolean wasSwapFollowedByAttack() {
        BufferedInput swap = null;
        BufferedInput attack = null;

        for (BufferedInput input : inputs) {
            if ("swap".equals(input.type) || "hotbar".equals(input.type)) {
                swap = input;
            }
            if ("attack".equals(input.type)) {
                attack = input;
                break;
            }
        }

        if (swap == null || attack == null) {
            return false;
        }

        int delta = attack.tick - swap.tick;
        int maxLen = ModConfig.getConfig().bufferLength;
        return delta >= 0 && delta <= maxLen;
    }

    private void record(String type, int slot) {
        inputs.addFirst(new BufferedInput(type, globalTick, slot));
    }

    private void purge() {
        int maxLen = ModConfig.getConfig().bufferLength;
        while (!inputs.isEmpty() && (globalTick - inputs.peekLast().tick) > maxLen) {
            inputs.pollLast();
        }
    }

    public int getSize() {
        return inputs.size();
    }

    public void clear() {
        inputs.clear();
    }

    private int getCurrentSlot() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return -1;
        }
        return client.player.getInventory().getSelectedSlot();
    }
}
