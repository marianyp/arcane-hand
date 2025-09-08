package dev.mariany.arcanehand.util;

import dev.mariany.arcanehand.server.network.MiningState;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface BlockBreaker {
    static boolean canHarvest(PlayerEntity player, BlockPos pos) {
        World world = player.getWorld();
        BlockState state = world.getBlockState(pos);
        return !state.isAir() && player.canHarvest(state) && world.getWorldBorder().contains(pos);
    }

    static boolean attemptBreak(World world, BlockPos pos, PlayerEntity player) {
        if (!player.isSneaking() && canHarvest(player, pos)) {
            breakInRadius(world, player, true);
            return true;
        }

        return false;
    }

    static void breakInRadius(World world, PlayerEntity player, boolean damageTool) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ServerWorld serverWorld = serverPlayer.getWorld();
            ServerPlayerInteractionManager interactionManager = serverPlayer.interactionManager;

            if (serverPlayer.interactionManager instanceof MiningState miningState) {
                miningState.arcaneHand$setIsMining(true);

                List<BlockPos> brokenBlocks = BlockBreaker.collectPositions(world, player);

                for (BlockPos pos : brokenBlocks) {
                    BlockState state = world.getBlockState(pos);
                    BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;

                    if (player.canHarvest(state) && !state.isAir()) {
                        state.getBlock().onBreak(world, pos, state, player);
                        if (!interactionManager.tryBreakBlock(pos)) {
                            continue;
                        }

                        boolean shouldContinue = PlayerBlockBreakEvents.BEFORE
                                .invoker()
                                .beforeBlockBreak(
                                        world,
                                        player,
                                        pos,
                                        state,
                                        world.getBlockEntity(pos)
                                );

                        if (shouldContinue) {
                            if (world.removeBlock(pos, false)) {
                                state.getBlock().onBroken(world, pos, state);
                            }

                            if (!player.isCreative()) {
                                Block.getDroppedStacks(
                                        state,
                                        serverWorld,
                                        pos,
                                        blockEntity,
                                        player,
                                        player.getMainHandStack()
                                ).forEach(stack -> Block.dropStack(world, pos, stack));

                                state.onStacksDropped(serverWorld, pos, player.getMainHandStack(), true);

                                if (damageTool) {
                                    ItemStack itemStack = player.getMainHandStack();
                                    boolean usingEffectiveTool = player.canHarvest(state);

                                    itemStack.postMine(world, state, pos, player);

                                    if (usingEffectiveTool) {
                                        player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
                                        player.addExhaustion(0.005F);
                                    }
                                }
                            }
                        }
                    }
                }

                miningState.arcaneHand$setIsMining(false);
            }
        }
    }

    static List<BlockPos> collectPositions(World world, PlayerEntity player) {
        int radius = AHHelper.getMineRadius(player);
        ArrayList<BlockPos> potentialBrokenBlocks = new ArrayList<>();

        Vec3d cameraPos = player.getCameraPosVec(1);
        Vec3d rotation = player.getRotationVec(1);
        double reachDistance = player.getBlockInteractionRange();

        Vec3d combined = cameraPos.add(
                rotation.x * reachDistance,
                rotation.y * reachDistance,
                rotation.z * reachDistance
        );

        BlockHitResult blockHitResult = world.raycast(
                new RaycastContext(
                        cameraPos,
                        combined,
                        RaycastContext.ShapeType.OUTLINE,
                        RaycastContext.FluidHandling.NONE,
                        player
                )
        );

        if (blockHitResult.getType().equals(HitResult.Type.BLOCK)) {
            Direction.Axis axis = blockHitResult.getSide().getAxis();
            ArrayList<Vec3i> positions = new ArrayList<>();

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        positions.add(new Vec3i(x, y, z));
                    }
                }
            }

            BlockPos origin = blockHitResult.getBlockPos();

            for (Vec3i pos : positions) {
                if (axis == Direction.Axis.Y) {
                    if (pos.getY() == 0) {
                        potentialBrokenBlocks.add(origin.add(pos));
                    }
                } else if (axis == Direction.Axis.X) {
                    if (pos.getX() == 0) {
                        potentialBrokenBlocks.add(origin.add(pos));
                    }
                } else if (axis == Direction.Axis.Z) {
                    if (pos.getZ() == 0) {
                        potentialBrokenBlocks.add(origin.add(pos));
                    }
                }
            }
        }

        return potentialBrokenBlocks;
    }
}
