package dev.mariany.arcanehand.datagen;

import dev.mariany.arcanehand.block.AHBlocks;
import dev.mariany.arcanehand.item.AHItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.TexturedModel;

public class AHModelProvider extends FabricModelProvider {
    public AHModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSingleton(AHBlocks.ARCANE_CONSOLE, TexturedModel.CUBE_BOTTOM_TOP);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.registerWithDyeableOverlay(AHItems.GAUNTLET);
    }
}
