package dev.mariany.arcanehand.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

public class AHHelper {
    public static boolean isThirdPerson(ItemDisplayContext context) {
        return context.equals(ItemDisplayContext.THIRD_PERSON_LEFT_HAND) ||
                context.equals(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
    }

    public static Arm getArm(Hand hand, PlayerEntity player) {
        return hand == Hand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
    }

    public static boolean pointInRect(double pointX, double pointY, int x, int y, int width, int height) {
        return pointX >= x && pointY >= y && pointX < x + width && pointY < y + height;
    }
}
