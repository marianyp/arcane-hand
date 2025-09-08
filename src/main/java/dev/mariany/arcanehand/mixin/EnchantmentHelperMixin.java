package dev.mariany.arcanehand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mariany.arcanehand.enchantment.AHEnchantments;
import dev.mariany.arcanehand.item.GauntletItem;
import dev.mariany.arcanehand.tag.AHTags;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @WrapOperation(
            method = "forEachEnchantment(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EquipmentSlot;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/enchantment/EnchantmentHelper$ContextAwareConsumer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/Enchantment;slotMatches(Lnet/minecraft/entity/EquipmentSlot;)Z"
            )
    )
    private static boolean wrapForEachEnchantment(
            Enchantment enchantment,
            EquipmentSlot slot,
            Operation<Boolean> original,
            @Local(index = 0, argsOnly = true) ItemStack stack
    ) {
        if (stack.getItem() instanceof GauntletItem && slot.getType().equals(EquipmentSlot.Type.HAND)) {
            for (AttributeModifierSlot modifierSlot : enchantment.definition().slots()) {
                if (modifierSlot.matches(EquipmentSlot.CHEST)) {
                    return true;
                }
            }
        }

        return original.call(enchantment, slot);
    }

    @Inject(method = "getLevel", at = @At(value = "RETURN"), cancellable = true)
    private static void injectGetLevel(
            RegistryEntry<Enchantment> enchantment,
            ItemStack stack,
            CallbackInfoReturnable<Integer> cir
    ) {
        if (cir.getReturnValue() <= 0 && enchantment.isIn(AHTags.Enchantments.MATCHES_ABUNDANCE)) {
            ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(
                    DataComponentTypes.ENCHANTMENTS,
                    ItemEnchantmentsComponent.DEFAULT
            );

            Set<Object2IntMap.Entry<RegistryEntry<Enchantment>>> enchantmentEntries =
                    itemEnchantmentsComponent.getEnchantmentEntries();

            int level = 0;

            for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : enchantmentEntries) {
                Optional<RegistryKey<Enchantment>> optionalKey = entry.getKey().getKey();

                if (optionalKey.isPresent()) {
                    if (optionalKey.get().getValue().equals(AHEnchantments.ABUNDANCE.getValue())) {
                        level = entry.getIntValue();
                        break;
                    }
                }
            }

            if (level != 0) {
                cir.setReturnValue(level);
            }
        }
    }
}
