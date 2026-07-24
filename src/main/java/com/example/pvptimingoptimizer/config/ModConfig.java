package com.example.pvptimingoptimizer.config;

public class ModConfig {
    public boolean enabled = true;
    public boolean debugHud = false;

    public boolean predictiveSwapEnabled = true;
    public PredictionStrength predictionStrength = PredictionStrength.HIGH;
    public int swapOffset = 1;
    public boolean autoAttackOnSwap = true;

    public boolean pingCompensationEnabled = true;
    public int latencyMultiplier = 150;
    public boolean showPingData = false;

    public boolean inputBufferEnabled = true;
    public int bufferLength = 5;

    public int attackWindowTicks = 25;

    public enum PredictionStrength {
        LOW, MEDIUM, HIGH
    }

    public static ModConfig getConfig() {
        return ConfigManager.getConfig();
    }

    public void save() {
        ConfigManager.saveConfig();
    }
}
