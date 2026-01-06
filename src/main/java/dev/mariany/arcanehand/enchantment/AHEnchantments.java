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
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EnchantmentTags;

public interface AHEnchantments {
    RegistryKey<Enchantment> ABUNDANCE = of("abundance");
    RegistryKey<Enchantment> BLAZE = of("blaze");
    RegistryKey<Enchantment> EXCAVATE = of("excavate");
    RegistryKey<Enchantment> VEIN_MINING = of("vein_mining");
    RegistryKey<Enchantment> COLLECT = of("collect");

    static void bootstrap(Registerable<Enchantment> registry) {
        RegistryEntryLookup<Enchantment> enchantmentRegistry = registry.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        RegistryEntryLookup<EntityType<?>> entityRegistry = registry.getRegistryLookup(RegistryKeys.ENTITY_TYPE);
        RegistryEntryLookup<Item> itemRegistry = registry.getRegistryLookup(RegistryKeys.ITEM);

        register(
                registry,
                ABUNDANCE,
                Enchantment.builder(
                                   Enchantment.definition(
                                           itemRegistry.getOrThrow(AHTags.Items.GAUNTLET_ENCHANTABLE),
                                           2,
                                           3,
                                           Enchantment.leveledCost(15, 9),
                                           Enchantment.leveledCost(65, 9),
                                           4,
                                           AttributeModifierSlot.MAINHAND
                                   )
                           )
                           .addEffect(
                                   EnchantmentEffectComponentTypes.EQUIPMENT_DROPS,
                                   EnchantmentEffectTarget.ATTACKER,
                                   EnchantmentEffectTarget.VICTIM,
                                   new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.01F)),
                                   EntityPropertiesLootCondition.builder(
                                           LootContext.EntityReference.ATTACKER,
                                           EntityPredicate.Builder.create().type(
                                                   EntityTypePredicate.create(entityRegistry, EntityType.PLAYER)
                                           )
                                   )
                           )
                           .exclusiveSet(enchantmentRegistry.getOrThrow(EnchantmentTags.MINING_EXCLUSIVE_SET))
        );

        register(
                registry,
                BLAZE,
                Enchantment.builder(
                                   Enchantment.definition(
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

        register(
                registry,
                EXCAVATE,
                Enchantment.builder(
                                   Enchantment.definition(
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
                                   AHEnchantmentEffectComponents.MINE_RADIUS,
                                   new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1))
                           )
        );

        register(
                registry,
                VEIN_MINING,
                Enchantment.builder(
                                   Enchantment.definition(
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
                                   AHEnchantmentEffectComponents.VEIN_MINE,
                                   new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(50))
                           )
                           .exclusiveSet(enchantmentRegistry.getOrThrow(AHTags.Enchantments.MULTI_MINING_EXCLUSIVE_SET))
        );

        register(
                registry,
                COLLECT,
                Enchantment.builder(
                                   Enchantment.definition(
                                           itemRegistry.getOrThrow(AHTags.Items.GAUNTLET_ENCHANTABLE),
                                           2,
                                           1,
                                           Enchantment.constantCost(30),
                                           Enchantment.constantCost(80),
                                           4,
                                           AttributeModifierSlot.MAINHAND
                                   )
                           )
                           .addEffect(AHEnchantmentEffectComponents.COLLECT)
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
