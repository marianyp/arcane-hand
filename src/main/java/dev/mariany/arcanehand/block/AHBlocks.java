package dev.mariany.arcanehand.block;

import dev.mariany.arcanehand.ArcaneHand;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class AHBlocks {
    public static final Block ARCANE_CONSOLE = register(
            "arcane_console",
            ArcaneConsoleBlock::new,
            AbstractBlock.Settings.create()
                                  .mapColor(DyeColor.GRAY)
                                  .instrument(NoteBlockInstrument.BASEDRUM)
                                  .requiresTool()
                                  .strength(5, 6)
                                  .luminance(state -> 6)
    );

    private static Block register(
            String name,
            Function<AbstractBlock.Settings, Block> factory,
            AbstractBlock.Settings settings
    ) {
        final Identifier identifier = ArcaneHand.id(name);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.register(block);

        return block;
    }

    public static void bootstrap() {
        ArcaneHand.LOGGER.info("Registering Blocks for {}", ArcaneHand.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.addAfter(Items.ENCHANTING_TABLE, ARCANE_CONSOLE);
        });
    }
}
