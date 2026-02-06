package dev.mariany.arcanehand.enchantment;

import dev.mariany.arcanehand.item.GauntletItem;
import dev.mariany.arcanehand.tag.AHTags;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Optional;
import java.util.Set;

public final class EnchantmentMatchFixer {
    private EnchantmentMatchFixer() {
    }

    public static boolean isValidEnchantment(ItemStack stack, EquipmentSlot slot, Enchantment enchantment) {
        if (stack.getItem() instanceof GauntletItem && slot.getType().equals(EquipmentSlot.Type.HAND)) {
            for (AttributeModifierSlot modifierSlot : enchantment.definition().slots()) {
                if (modifierSlot.matches(EquipmentSlot.CHEST)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Integer getFixedLevel(int level, RegistryEntry<Enchantment> enchantment, ItemStack stack) {
        if (level <= 0 && enchantment.isIn(AHTags.Enchantments.MATCHES_ABUNDANCE)) {
            ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(
                    DataComponentTypes.ENCHANTMENTS,
                    ItemEnchantmentsComponent.DEFAULT
            );

            Set<Object2IntMap.Entry<RegistryEntry<Enchantment>>> enchantmentEntries =
                    itemEnchantmentsComponent.getEnchantmentEntries();

            int correctLevel = 0;

            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : enchantmentEntries) {
                Optional<RegistryKey<Enchantment>> optionalKey = entry.getKey().getKey();

                if (optionalKey.isPresent()) {
                    if (optionalKey.get().getValue().equals(AHEnchantments.ABUNDANCE.getValue())) {
                        correctLevel = entry.getIntValue();
                        break;
                    }
                }
            }

            if (correctLevel != 0) {
                return correctLevel;
            }
        }

        return null;
    }
}
