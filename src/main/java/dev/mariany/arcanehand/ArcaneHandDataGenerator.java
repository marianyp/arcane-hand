package dev.mariany.arcanehand;

import dev.mariany.arcanehand.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ArcaneHandDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(AHAdvancementsProvider::new);
        pack.addProvider(AHBlockLootTableProvider::new);
        pack.addProvider(AHBlockTagProvider::new);
        pack.addProvider(AHEnchantmentTagProvider::new);
        pack.addProvider(AHItemTagProvider::new);
        pack.addProvider(AHModelProvider::new);
        pack.addProvider(AHRecipeProvider::new);
    }
}
