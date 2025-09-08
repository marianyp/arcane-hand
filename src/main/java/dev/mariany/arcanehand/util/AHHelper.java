package dev.mariany.arcanehand.util;

import dev.mariany.arcanehand.component.AHEnchantmentEffectComponents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.List;

public interface AHHelper {
    static boolean isThirdPerson(ItemDisplayContext context) {
        return context.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND) ||
                context.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
    }

    static Arm getArm(Hand hand, PlayerEntity player) {
        return hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
    }

    static boolean pointInRect(double pointX, double pointY, int x, int y, int width, int height) {
        return pointX >= x && pointY >= y && pointX < x + width && pointY < y + height;
    }

    static boolean canMineRadius(ItemStack stack) {
        return getMineRadius(stack) > 0;
    }

    static int getMineRadius(ItemStack stack) {
        MutableFloat mineRadius = new MutableFloat();

        EnchantmentHelper.forEachEnchantment(
                stack,
                (enchantment, level) -> modifyMineRadius(
                        enchantment.value(),
                        level,
                        mineRadius
                )
        );

        return Math.max(0, mineRadius.intValue());
    }

    private static void modifyMineRadius(
            Enchantment enchantment,
            int level,
            MutableFloat repairWithExperience
    ) {
        List<EnchantmentEffectEntry<EnchantmentValueEffect>> effectEntries = enchantment.getEffect(
                AHEnchantmentEffectComponents.MINE_RADIUS
        );

        for (EnchantmentEffectEntry<EnchantmentValueEffect> enchantmentEffectEntry : effectEntries) {
            repairWithExperience.setValue(enchantmentEffectEntry.effect().apply(
                    level,
                    Random.create(),
                    repairWithExperience.getValue()
            ));
        }
    }
}
