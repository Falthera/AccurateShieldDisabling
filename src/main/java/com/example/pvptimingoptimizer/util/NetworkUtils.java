package com.example.pvptimingoptimizer.util;

import com.example.pvptimingoptimizer.PvPClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

public class NetworkUtils {
    public static int getPing() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return 0;
        }

        ClientPlayNetworkHandler handler = client.getNetworkHandler();
        if (handler == null) {
            return 0;
        }

        PlayerListEntry entry = handler.getPlayerListEntry(client.player.getUuid());
        if (entry == null) {
            entry = handler.getPlayerListEntry(client.player.getGameProfile().getName());
        }

        return entry != null ? entry.getLatency() : 0;
    }

    public static float tickRate() {
        return 20.0f;
    }

    public static long ticksFromPing(int ping) {
        if (ping <= 0) {
            return 0L;
        }
        return Math.max(0L, Math.round(ping / 1000.0 * 20.0));
    }
}
