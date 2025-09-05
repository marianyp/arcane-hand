package dev.mariany.arcanehand.packet.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerBoundPackets {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(EnchantmentSelectedPayload.ID, EnchantmentSelectedPayload::apply);
    }
}
