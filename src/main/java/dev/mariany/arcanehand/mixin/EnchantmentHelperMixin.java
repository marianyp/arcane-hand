package dev.mariany.arcanehand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mariany.arcanehand.item.GauntletItem;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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
}
