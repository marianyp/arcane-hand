package dev.mariany.arcanehand.datagen;

import dev.mariany.arcanehand.block.AHBlocks;
import dev.mariany.arcanehand.item.AHItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;

public class AHModelProvider extends FabricModelProvider {
    public AHModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(AHBlocks.ARCANE_CONSOLE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.registerWithDyeableOverlay(AHItems.GAUNTLET);
    }
}
