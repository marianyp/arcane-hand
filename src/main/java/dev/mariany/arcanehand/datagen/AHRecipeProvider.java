package dev.mariany.arcanehand.datagen;

import dev.mariany.arcanehand.block.AHBlocks;
import dev.mariany.arcanehand.item.AHItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class AHRecipeProvider extends FabricRecipeProvider {
    public AHRecipeProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(
            RegistryWrapper.WrapperLookup wrapperLookup,
            RecipeExporter recipeExporter
    ) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                this.createShaped(RecipeCategory.TOOLS, AHItems.GAUNTLET)
                    .pattern("LLL")
                    .pattern("LLL")
                    .pattern(" N ")
                    .input('L', Items.LEATHER)
                    .input('N', Items.NETHERITE_INGOT)
                    .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                    .offerTo(this.exporter);

                this.createShaped(RecipeCategory.MISC, AHBlocks.ARCANE_CONSOLE)
                    .pattern("SDS")
                    .pattern("DAD")
                    .pattern("SDS")
                    .input('A', Items.AMETHYST_SHARD)
                    .input('D', Items.DIAMOND)
                    .input('S', Items.SMOOTH_STONE)
                    .criterion(hasItem(AHItems.GAUNTLET), conditionsFromItem(AHItems.GAUNTLET))
                    .offerTo(this.exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Arcane Hand Recipes";
    }
}
