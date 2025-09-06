package dev.mariany.arcanehand.block;

import dev.mariany.arcanehand.screen.ArcaneConsoleScreenHandler;
import dev.mariany.arcanehand.stat.AHStats;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArcaneConsoleBlock extends Block {
    private static final Text TITLE = Text.translatable("container.arcanehand.arcane_console");

    public ArcaneConsoleBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(AHStats.INTERACT_WITH_ARCANE_CONSOLE);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, player) -> new ArcaneConsoleScreenHandler(
                        syncId,
                        inventory,
                        ScreenHandlerContext.create(world, pos)
                ),
                TITLE
        );
    }
}
