package dev.mariany.arcanehand;

import dev.mariany.arcanehand.item.GauntletItem;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

public class AHHelpers {
    public static final int DEFAULT_GAUNTLET_COLOR = -6265536;

    public static boolean isGauntlet(ItemStack stack) {
        return stack.getItem() instanceof GauntletItem;
    }

    public static boolean isThirdPerson(ItemDisplayContext context) {
        return context.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND) ||
                context.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
    }

    public static int convertLevelsToExperience(int level) {
        if (level <= 16) {
            return (int) (Math.pow(level, 2) + level * 6);
        }

        if (level <= 31) {
            return (int) (Math.pow(level, 2) * 2.5 - 40.5 * level + 360);
        }

        return (int) (Math.pow(level, 2) * 4.5 - 162.5 * level + 2220);
    }

    public static Arm getArm(Hand hand, PlayerEntity player) {
        return hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
    }

    public static int getGauntletColor(ItemStack stack) {
        if (isGauntlet(stack)) {
            int color = DyedColorComponent.getColor(stack, 0);
            return color != 0 ? color : DEFAULT_GAUNTLET_COLOR;
        }

        return DEFAULT_GAUNTLET_COLOR;
    }

    public static boolean pointInRect(double pointX, double pointY, int x, int y, int width, int height) {
        return pointX >= x && pointY >= y && pointX < x + width && pointY < y + height;
    }
}
