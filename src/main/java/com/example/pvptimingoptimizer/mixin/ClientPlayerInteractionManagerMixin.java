package com.example.pvptimingoptimizer.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (!ShieldDisableBypass.shouldBypass()) {
            return;
        }

        Object instance = this;
        Class<?> clazz = instance.getClass();

        while (clazz != null) {
            String[] names = {"attackCooldown", "field_18725", "cooldown"};
            for (String name : names) {
                try {
                    Field field = clazz.getDeclaredField(name);
                    field.setAccessible(true);
                    field.setInt(instance, 0);
                    return;
                } catch (NoSuchFieldException e) {
                    // try next name
                } catch (Exception e) {
                    return;
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
