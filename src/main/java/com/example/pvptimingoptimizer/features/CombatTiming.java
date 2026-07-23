package com.example.pvptimingoptimizer.features;

import com.example.pvptimingoptimizer.config.ModConfig;
import com.example.pvptimingoptimizer.util.NetworkUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.AxeItem;

public class CombatTiming {
    private String weaponType = "none";
    private float cooldown = 1.0f;
    private float cooldownPerTick = 0.1f;
    private int recordedSwapTick = -1;
    private boolean swapWasCombat = false;

    public void tick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }

        ClientPlayerEntity player = client.player;
        ItemStack stack = player.getMainHandStack();

        if (stack.isEmpty()) {
            weaponType = "none";
            cooldown = 0f;
            return;
        }

        if (stack.getItem() instanceof SwordItem) {
            weaponType = "sword";
            cooldownPerTick = player.getAttackCooldownProgressPerTick();
        } else if (stack.getItem() instanceof AxeItem) {
            weaponType = "axe";
            cooldownPerTick = player.getAttackCooldownProgressPerTick();
        } else {
            weaponType = "other";
            cooldownPerTick = 0f;
        }

        cooldown = player.getAttackCooldownProgress(0f);
    }

    public boolean isCombatWeapon() {
        return "sword".equals(weaponType) || "axe".equals(weaponType);
    }

    public String getWeaponType() {
        return weaponType;
    }

    public float getCooldownProgress() {
        return cooldown;
    }

    public float getCooldownPerTick() {
        return cooldownPerTick;
    }

    public boolean canFullDamageAttack() {
        return cooldown >= 1.0f;
    }

    public void markSwap(int tick) {
        recordedSwapTick = tick;
        swapWasCombat = isCombatWeapon();
    }

    public int getTicksSinceSwap(int currentTick) {
        if (recordedSwapTick < 0) {
            return -1;
        }
        return currentTick - recordedSwapTick;
    }

    public boolean wasSwapCombat() {
        return swapWasCombat;
    }
}
