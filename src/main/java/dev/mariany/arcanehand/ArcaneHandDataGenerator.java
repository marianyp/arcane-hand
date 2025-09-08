package dev.mariany.arcanehand;

import dev.mariany.arcanehand.datagen.*;
import dev.mariany.arcanehand.enchantment.AHEnchantments;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class ArcaneHandDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(AHAdvancementsProvider::new);
        pack.addProvider(AHBlockLootTableProvider::new);
        pack.addProvider(AHBlockTagProvider::new);
        pack.addProvider(AHEnchantmentProvider::new);
        pack.addProvider(AHEnchantmentTagProvider::new);
        pack.addProvider(AHItemTagProvider::new);
        pack.addProvider(AHModelProvider::new);
        pack.addProvider(AHRecipeProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.ENCHANTMENT, AHEnchantments::bootstrap);
    }
}
