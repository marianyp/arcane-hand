package dev.mariany.arcanehand.event;

import dev.mariany.arcanehand.item.GauntletItem;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class AttackBlockHandler {
    public static void bootstrap() {
        AttackBlockCallback.EVENT.register(AttackBlockHandler::onAttackBlock);
    }

    private static ActionResult onAttackBlock(
            PlayerEntity player,
            World world,
            Hand hand,
            BlockPos pos,
            Direction direction
    ) {
        if(!GauntletItem.hasNeededDurability(world, pos, player.getStackInHand(hand))) {
            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }
}
