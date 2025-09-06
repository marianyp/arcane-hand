package dev.mariany.arcanehand;

import dev.mariany.arcanehand.block.AHBlocks;
import dev.mariany.arcanehand.component.AHComponents;
import dev.mariany.arcanehand.item.AHItems;
import dev.mariany.arcanehand.loot.LootTableModifiers;
import dev.mariany.arcanehand.packet.AHPackets;
import dev.mariany.arcanehand.packet.serverbound.ServerBoundPackets;
import dev.mariany.arcanehand.screen.AHScreenHandlers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcaneHand implements ModInitializer {
    public static final String MOD_ID = "arcanehand";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }

    @Override
    public void onInitialize() {
        registerPackets();
        AHComponents.bootstrap();
        AHItems.bootstrap();
        AHScreenHandlers.bootstrap();
        AHBlocks.bootstrap();
        LootTableModifiers.modifyLootTables();
    }

    private void registerPackets() {
        AHPackets.register();
        ServerBoundPackets.init();
    }
}