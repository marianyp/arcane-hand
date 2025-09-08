package dev.mariany.arcanehand.enchantment;

import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.component.AHEnchantmentEffectComponents;
import dev.mariany.arcanehand.tag.AHTags;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.entity.IgniteEnchantmentEffect;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public interface AHEnchantments {
    RegistryKey<Enchantment> BLAZE = of("blaze");

    static void bootstrap(Registerable<Enchantment> registry) {
        RegistryEntryLookup<Item> itemRegistry = registry.getRegistryLookup(RegistryKeys.ITEM);

        register(
                registry,
                BLAZE,
                Enchantment.builder(
                                   Enchantment.definition(
                                           itemRegistry.getOrThrow(AHTags.Items.BLAZE_ENCHANTABLE),
                                           itemRegistry.getOrThrow(AHTags.Items.GAUNTLET_ENCHANTABLE),
                                           2,
                                           1,
                                           Enchantment.constantCost(30),
                                           Enchantment.constantCost(80),
                                           4,
                                           AttributeModifierSlot.MAINHAND
                                   )
                           )
                           .addEffect(
                                   EnchantmentEffectComponentTypes.POST_ATTACK,
                                   EnchantmentEffectTarget.ATTACKER,
                                   EnchantmentEffectTarget.VICTIM,
                                   new IgniteEnchantmentEffect(EnchantmentLevelBasedValue.constant(8)),
                                   DamageSourcePropertiesLootCondition.builder(
                                           DamageSourcePredicate.Builder.create().isDirect(true)
                                   )
                           )
                           .addEffect(AHEnchantmentEffectComponents.SMELT_DROPS)
        );
    }

    private static void register(
            Registerable<Enchantment> registry,
            RegistryKey<Enchantment> key,
            Enchantment.Builder builder
    ) {
        registry.register(key, builder.build(key.getValue()));
    }

    private static RegistryKey<Enchantment> of(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, ArcaneHand.id(id));
    }
}
