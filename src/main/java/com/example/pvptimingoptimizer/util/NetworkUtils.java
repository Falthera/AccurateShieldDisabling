package com.example.pvptimingoptimizer.util;

import com.example.pvptimingoptimizer.client.PvPClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

public class NetworkUtils {
    public static int getPing() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getNetworkHandler() == null) {
            return 0;
        }

        ClientPlayerEntity player = client.player;
        if (player == null) {
            return 0;
        }

        ClientPlayNetworkHandler handler = client.getNetworkHandler();
        PlayerListEntry info = handler.getPlayerListEntry(player.getUuid());
        if (info == null) {
            info = handler.getPlayerListEntry(player.getName().getString());
        }

        return info != null ? info.getLatency() : 0;
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
