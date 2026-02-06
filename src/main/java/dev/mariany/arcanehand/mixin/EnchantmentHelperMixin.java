package dev.mariany.arcanehand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mariany.arcanehand.enchantment.EnchantmentMatchFixer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        if (EnchantmentMatchFixer.isValidEnchantment(stack, slot, enchantment)) {
            return true;
        }

        return original.call(enchantment, slot);
    }

    @Inject(method = "getLevel", at = @At(value = "RETURN"), cancellable = true)
    private static void injectGetLevel(
            RegistryEntry<Enchantment> enchantment,
            ItemStack stack,
            CallbackInfoReturnable<Integer> cir
    ) {
        Integer fixedLevel = EnchantmentMatchFixer.getFixedLevel(cir.getReturnValue(), enchantment, stack);

        if (fixedLevel != null) {
            cir.setReturnValue(fixedLevel);
        }
    }
}
