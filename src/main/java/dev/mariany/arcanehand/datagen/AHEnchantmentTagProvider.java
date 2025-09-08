package dev.mariany.arcanehand.datagen;

import dev.mariany.arcanehand.enchantment.AHEnchantments;
import dev.mariany.arcanehand.tag.AHTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.tag.EnchantmentTagProvider;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;

import java.util.concurrent.CompletableFuture;

public class AHEnchantmentTagProvider extends EnchantmentTagProvider {
    public AHEnchantmentTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.builder(EnchantmentTags.SMELTS_LOOT).add(AHEnchantments.BLAZE);

        this.builder(AHTags.Enchantments.GAUNTLET_BLACKLIST)
            .add(Enchantments.FIRE_ASPECT)
            .add(Enchantments.FORTUNE)
            .add(Enchantments.LOOTING)
            .add(Enchantments.PROTECTION)
            .add(Enchantments.SWEEPING_EDGE);

        this.builder(AHTags.Enchantments.MATCHES_ABUNDANCE)
            .add(Enchantments.FORTUNE)
            .add(Enchantments.LOOTING);

        this.builder(AHTags.Enchantments.IN_ARCANE_CONSOLE)
            .addOptionalTag(EnchantmentTags.IN_ENCHANTING_TABLE)
            .add(AHEnchantments.ABUNDANCE)
            .add(AHEnchantments.BLAZE)
            .add(AHEnchantments.EXCAVATE);
    }
}