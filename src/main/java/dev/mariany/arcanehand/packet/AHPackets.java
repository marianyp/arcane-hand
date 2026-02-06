package dev.mariany.arcanehand.packet;

import dev.mariany.arcanehand.packet.serverbound.EnchantmentSelectedPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;

public final class AHPackets {
    private AHPackets() {
    }

    public static void register() {
        clientbound(PayloadTypeRegistry.playS2C());
        serverbound(PayloadTypeRegistry.playC2S());
    }

    private static void clientbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
    }

    private static void serverbound(PayloadTypeRegistry<RegistryByteBuf> registry) {
        registry.register(EnchantmentSelectedPayload.ID, EnchantmentSelectedPayload.CODEC);
    }
}
