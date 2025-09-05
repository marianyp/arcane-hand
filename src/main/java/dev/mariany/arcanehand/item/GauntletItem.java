package dev.mariany.arcanehand.item;

import dev.mariany.arcanehand.AHHelpers;
import dev.mariany.arcanehand.component.AHComponents;
import dev.mariany.arcanehand.component.enchantment.EnchantmentProgression;
import dev.mariany.arcanehand.component.enchantment.EnchantmentProgressionComponent;
import dev.mariany.arcanehand.component.enchantment.EnchantmentProgressionState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class GauntletItem extends Item {
    private static final List<TagKey<Item>> GAUNTLET_ENCHANTABLE = List.of(
            ItemTags.ARMOR_ENCHANTABLE,
            ItemTags.DURABILITY_ENCHANTABLE,
            ItemTags.FIRE_ASPECT_ENCHANTABLE,
            ItemTags.MINING_ENCHANTABLE,
            ItemTags.MINING_LOOT_ENCHANTABLE,
            ItemTags.SWORD_ENCHANTABLE,
            ItemTags.WEAPON_ENCHANTABLE
    );

    public GauntletItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }

    public static int getEnchantmentCount(ItemStack stack) {
        return Math.toIntExact(
                stack.getOrDefault(AHComponents.ENCHANTMENT_PROGRESSION, EnchantmentProgressionComponent.DEFAULT)
                     .enchantments()
                     .values()
                     .stream()
                     .filter(EnchantmentProgression::isEnabled)
                     .count()
        );
    }

    public static int getSelectedEnchantmentIndex(ItemStack stack) {
        return stack.getOrDefault(AHComponents.ENCHANTMENT_PROGRESSION, EnchantmentProgressionComponent.DEFAULT)
                    .selectedEnchantment();
    }

    public static void setSelectedEnchantmentIndex(ItemStack stack, int selectedEnchantmentIndex) {
        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.get(
                AHComponents.ENCHANTMENT_PROGRESSION
        );

        if (enchantmentProgressionComponent != null) {
            stack.set(
                    AHComponents.ENCHANTMENT_PROGRESSION,
                    new EnchantmentProgressionComponent(
                            enchantmentProgressionComponent.enchantments(),
                            selectedEnchantmentIndex
                    )
            );
        }
    }

    public static boolean isAcceptable(Enchantment enchantment) {
        return enchantment
                .definition()
                .supportedItems()
                .getTagKey()
                .map(GAUNTLET_ENCHANTABLE::contains)
                .orElse(false);
    }

    public static void applyProgress(
            DynamicRegistryManager dynamicRegistryManager,
            RegistryKey<Enchantment> enchantmentKey,
            EnchantmentProgression progress,
            ItemStack stack
    ) {
        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.getOrDefault(
                AHComponents.ENCHANTMENT_PROGRESSION,
                EnchantmentProgressionComponent.DEFAULT
        );

        Map<RegistryKey<Enchantment>, EnchantmentProgression> progression =
                new HashMap<>(enchantmentProgressionComponent.enchantments());

        progression.put(enchantmentKey, progress);

        applyProgress(dynamicRegistryManager, progression, stack);
    }

    public static void applyProgress(
            DynamicRegistryManager dynamicRegistryManager,
            Map<RegistryKey<Enchantment>, EnchantmentProgression> progression,
            ItemStack stack
    ) {
        EnchantmentProgressionComponent progressionComponent =
                new EnchantmentProgressionComponent(progression).excludingUnset();

        stack.set(AHComponents.ENCHANTMENT_PROGRESSION, progressionComponent);
        stack.set(DataComponentTypes.ENCHANTMENTS, progressionComponent.toEnchantments(dynamicRegistryManager));
    }

    public static int progress(ServerWorld world, ItemStack gauntlet, int experience) {
        DynamicRegistryManager registryManager = world.getRegistryManager();
        Registry<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);

        EnchantmentProgressionComponent enchantmentProgressionComponent = gauntlet.getOrDefault(
                AHComponents.ENCHANTMENT_PROGRESSION,
                EnchantmentProgressionComponent.DEFAULT
        );

        if (enchantmentProgressionComponent.isEmpty()) {
            return experience;
        }

        Map<RegistryKey<Enchantment>, EnchantmentProgression> progression = new HashMap<>();

        List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> entries = new ArrayList<>(
                enchantmentProgressionComponent.enchantments().entrySet()
        );

        Collections.shuffle(entries, world.random::nextLong);

        int remaining = experience;
        int skipped = 0;

        for (Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry : entries) {
            RegistryKey<Enchantment> enchantmentKey = entry.getKey();
            EnchantmentProgression progress = entry.getValue();
            boolean enabled = progress.isEnabled();
            int level = progress.level();
            int nextLevel = level + 1;

            Optional<Enchantment> optionalEnchantment = enchantmentRegistry.getOptionalValue(enchantmentKey);

            if (optionalEnchantment.isPresent()) {
                Enchantment enchantment = optionalEnchantment.get();
                Enchantment.Definition definition = enchantment.definition();

                if (!enabled || level >= definition.maxLevel()) {
                    progression.put(enchantmentKey, progress);
                    ++skipped;
                } else {
                    int cost = definition.minCost().forLevel(nextLevel);
                    int previousExperience = progress.earnedExperience();
                    int target = AHHelpers.convertLevelsToExperience(cost);
                    int consumed;

                    if (remaining >= target - previousExperience) {
                        consumed = target - previousExperience;
                        remaining -= consumed;
                        progression.put(
                                enchantmentKey,
                                new EnchantmentProgression(
                                        nextLevel,
                                        0,
                                        EnchantmentProgressionState.ENABLED
                                )
                        );
                    } else {
                        consumed = remaining;
                        progression.put(
                                enchantmentKey,
                                new EnchantmentProgression(
                                        level,
                                        previousExperience + consumed,
                                        EnchantmentProgressionState.ENABLED
                                )
                        );
                        remaining = 0;
                    }

                    remaining = Math.max(0, remaining - consumed);
                }
            }
        }

        applyProgress(registryManager, progression, gauntlet);

        if (remaining > 0 && skipped < progression.size()) {
            return progress(world, gauntlet, remaining);
        }

        return remaining;
    }
}
