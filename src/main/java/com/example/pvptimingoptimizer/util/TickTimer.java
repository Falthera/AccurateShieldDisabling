package com.example.pvptimingoptimizer.util;

public class TickTimer {
    private int ticksElapsed;
    private int lastRecordedTick;

    public void tick() {
        ticksElapsed++;
    }

    public int getTicksSince(int worldTick) {
        return ticksElapsed - worldTick;
    }

    public int getElapsedTicks() {
        return ticksElapsed;
    }

    public void reset() {
        ticksElapsed = 0;
        lastRecordedTick = 0;
    }

    public int mark() {
        return ticksElapsed;
    }
}
