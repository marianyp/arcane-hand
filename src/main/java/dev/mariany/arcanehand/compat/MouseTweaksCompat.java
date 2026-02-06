package dev.mariany.arcanehand.compat;

import dev.mariany.arcanehand.component.AHComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class MouseTweaksCompat {
    private MouseTweaksCompat() {
    }

    public static ItemStack interceptOnMouseScrolled(ItemStack stack) {
        if (stack.contains(AHComponents.ENCHANTMENT_PROGRESSION)) {
            return Items.BUNDLE.getDefaultStack();
        }

        return stack;
    }
}
