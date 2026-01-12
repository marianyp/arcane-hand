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
                    .criterion(hasItem(Items.NETHERITE_INGOT), this.conditionsFromItem(Items.NETHERITE_INGOT))
                    .offerTo(this.exporter);

                this.createShaped(RecipeCategory.MISC, AHBlocks.ARCANE_CONSOLE, 9)
                    .pattern("CCC")
                    .pattern("CAC")
                    .pattern("CCC")
                    .input('C', Items.CHISELED_STONE_BRICKS)
                    .input('A', AHBlocks.ARCANE_CONSOLE)
                    .criterion(hasItem(AHBlocks.ARCANE_CONSOLE), this.conditionsFromItem(AHBlocks.ARCANE_CONSOLE))
                    .offerTo(this.exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "Arcane Hand Recipes";
    }
}
