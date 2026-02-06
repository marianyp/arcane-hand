package dev.mariany.arcanehand.tag;

import dev.mariany.arcanehand.ArcaneHand;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class AHTags {
    private AHTags() {
    }

    public static final class Items {
        public static final TagKey<Item> ARCANE_CONSOLE_MODIFIABLE = createTag("arcane_console_modifiable");
        public static final TagKey<Item> GAUNTLET_ENCHANTABLE = createTag("gauntlet_enchantable");
        public static final TagKey<Item> GAUNTLET_TOOL_MATERIALS = createTag("gauntlet_tool_materials");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, ArcaneHand.id(name));
        }
    }

    public static final class Blocks {
        public static final TagKey<Block> GAUNTLET_MINEABLE = createTag("gauntlet_mineable");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, ArcaneHand.id(name));
        }
    }

    public static final class Enchantments {
        public static final TagKey<Enchantment> GAUNTLET_BLACKLIST = createTag("gauntlet_blacklist");
        public static final TagKey<Enchantment> IN_ARCANE_CONSOLE = createTag("in_arcane_console");
        public static final TagKey<Enchantment> MATCHES_ABUNDANCE = createTag("matches_abundance");
        public static final TagKey<Enchantment> MULTI_MINING_EXCLUSIVE_SET = createTag("matches_abundance");

        private static TagKey<Enchantment> createTag(String name) {
            return TagKey.of(RegistryKeys.ENCHANTMENT, ArcaneHand.id(name));
        }
    }
}
