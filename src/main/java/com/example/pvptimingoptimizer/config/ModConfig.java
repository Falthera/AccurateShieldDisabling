package com.example.pvptimingoptimizer.config;

public class ModConfig {
    public boolean enabled = true;
    public boolean debugHud = false;

    public boolean predictiveSwapEnabled = true;
    public PredictionStrength predictionStrength = PredictionStrength.MEDIUM;
    public int swapOffset = 0;

    public boolean pingCompensationEnabled = true;
    public int latencyMultiplier = 100;
    public boolean showPingData = false;

    public boolean inputBufferEnabled = true;
    public int bufferLength = 3;

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
