package com.example.pvptimingoptimizer.mixin;

public class ShieldDisableBypass {
    private static boolean shouldBypassCooldown = false;

    public static void setBypass(boolean value) {
        shouldBypassCooldown = value;
    }

    public static boolean shouldBypass() {
        return shouldBypassCooldown;
    }
}
