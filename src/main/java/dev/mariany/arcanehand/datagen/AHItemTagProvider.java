package dev.mariany.arcanehand.datagen;

import dev.mariany.arcanehand.item.AHItems;
import dev.mariany.arcanehand.tag.AHTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class AHItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public AHItemTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(ItemTags.DYEABLE).add(AHItems.GAUNTLET);

        valueLookupBuilder(AHTags.Items.GAUNTLET_TOOL_MATERIALS).add(Items.LEATHER);
        valueLookupBuilder(AHTags.Items.ARCANE_CONSOLE_MODIFIABLE).add(AHItems.GAUNTLET);
        valueLookupBuilder(AHTags.Items.GAUNTLET_ENCHANTABLE).add(AHItems.GAUNTLET);
        valueLookupBuilder(AHTags.Items.BLAZE_ENCHANTABLE).add(AHItems.GAUNTLET);
    }
}