package dev.mariany.arcanehand.datagen;

import dev.mariany.arcanehand.block.AHBlocks;
import dev.mariany.arcanehand.tag.AHTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class AHBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public AHBlockTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(AHTags.Blocks.GAUNTLET_MINEABLE)
                .addOptionalTag(BlockTags.SHOVEL_MINEABLE)
                .addOptionalTag(BlockTags.PICKAXE_MINEABLE)
                .addOptionalTag(BlockTags.AXE_MINEABLE)
                .addOptionalTag(BlockTags.HOE_MINEABLE);

        valueLookupBuilder(BlockTags.PICKAXE_MINEABLE).add(AHBlocks.ARCANE_CONSOLE);
        valueLookupBuilder(BlockTags.PICKAXE_MINEABLE).add(AHBlocks.ARCANE_CONSOLE);
    }
}