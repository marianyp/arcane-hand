package dev.mariany.arcanehand.event;

import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.item.GauntletItem;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class AttackBlockHandler {
    private AttackBlockHandler() {
    }

    public static void bootstrap() {
        ArcaneHand.bootstrapLog("Attack Block Handler");

        AttackBlockCallback.EVENT.register(AttackBlockHandler::onAttackBlock);
    }

    private static ActionResult onAttackBlock(
            PlayerEntity player,
            World world,
            Hand hand,
            BlockPos pos,
            Direction direction
    ) {
        ItemStack stack = player.getStackInHand(hand);

        if (stack.getItem() instanceof GauntletItem && !GauntletItem.hasNeededDurability(world, pos, stack)) {
            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }
}
