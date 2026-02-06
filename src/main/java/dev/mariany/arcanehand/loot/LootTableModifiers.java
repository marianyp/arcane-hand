package dev.mariany.arcanehand.loot;

import dev.mariany.arcanehand.block.AHBlocks;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.List;

public final class LootTableModifiers {
    private static final List<RegistryKey<LootTable>> LOOT_TABLES = List.of(
            LootTables.BASTION_BRIDGE_CHEST,
            LootTables.BASTION_HOGLIN_STABLE_CHEST,
            LootTables.BASTION_OTHER_CHEST
    );

    private static final List<RegistryKey<LootTable>> CONSTANT_LOOT_TABLES = List.of(LootTables.BASTION_TREASURE_CHEST);

    private LootTableModifiers() {
    }

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register(
                (
                        key,
                        tableBuilder,
                        source,
                        registries
                ) -> {
                    if (source.isBuiltin()) {
                        Identifier tableId = key.getValue();

                        for (RegistryKey<LootTable> validTableKey : LOOT_TABLES) {
                            if (tableId.equals(validTableKey.getValue())) {
                                tableBuilder.pool(
                                        LootPool.builder()
                                                .rolls(ConstantLootNumberProvider.create(1F))
                                                .with(EmptyEntry.builder().weight(9))
                                                .with(ItemEntry.builder(AHBlocks.ARCANE_CONSOLE).weight(1))
                                );
                                break;
                            }
                        }

                        for (RegistryKey<LootTable> validTableKey : CONSTANT_LOOT_TABLES) {
                            if (tableId.equals(validTableKey.getValue())) {
                                tableBuilder.pool(
                                        LootPool.builder()
                                                .rolls(ConstantLootNumberProvider.create(1F))
                                                .with(ItemEntry.builder(AHBlocks.ARCANE_CONSOLE).weight(1))
                                );
                                break;
                            }
                        }
                    }
                }
        );
    }
}
