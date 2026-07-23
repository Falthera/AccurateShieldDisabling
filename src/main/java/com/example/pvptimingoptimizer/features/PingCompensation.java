package com.example.pvptimingoptimizer.features;

import com.example.pvptimingoptimizer.config.ModConfig;
import com.example.pvptimingoptimizer.util.NetworkUtils;

public class PingCompensation {
    private int currentPing;
    private float tickRate;
    private int tickOffset;

    public void update() {
        currentPing = NetworkUtils.getPing();
        tickRate = NetworkUtils.tickRate();

        if (tickRate <= 0f) {
            tickRate = 20f;
        }

        ModConfig config = ModConfig.getConfig();
        if (config.pingCompensationEnabled) {
            int rawTicks = (int) Math.round((currentPing / 1000.0) * (20.0) * (config.latencyMultiplier / 100.0));
            tickOffset = Math.max(0, rawTicks);
        } else {
            tickOffset = 0;
        }
    }

    public int getPing() {
        return currentPing;
    }

    public float getTickRate() {
        return tickRate;
    }

    public int getTickOffset() {
        return tickOffset;
    }
}
