package dev.mariany.arcanehand.logic;

import dev.mariany.arcanehand.server.network.MiningState;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface BlockBreaker {
    ExcavatorBreaker EXCAVATOR = new ExcavatorBreaker();
    VeinBreaker VEIN = new VeinBreaker();

    List<BlockPos> collectPositions(World world, PlayerEntity player);

    default List<BlockPos> collectPossiblePositions(World world, PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        ToolComponent toolComponent = stack.get(DataComponentTypes.TOOL);
        List<BlockPos> positions = this.collectPositions(world, player);

        if (toolComponent != null) {
            int remainingDamage = stack.getMaxDamage() - stack.getDamage();
            int maxBreaks = MathHelper.floor((float) remainingDamage / toolComponent.damagePerBlock()) - 1;

            if (maxBreaks > 0) {
                return positions.stream().limit(maxBreaks).toList();
            } else {
                return List.of();
            }
        }

        return positions;
    }

    static Optional<BlockBreaker> getBlockBreaker(PlayerEntity player) {
        if (ExcavatorBreaker.getMineRadius(player) > 0) {
            return Optional.of(EXCAVATOR);
        }

        if (VeinBreaker.getMaxVeinSize(player) > 0) {
            return Optional.of(VEIN);
        }

        return Optional.empty();
    }

    static BlockHitResult getBlockHitResult(PlayerEntity player) {
        Vec3d cameraPos = player.getCameraPosVec(1);
        Vec3d rotation = player.getRotationVec(1);
        double blockInteractionRange = player.getBlockInteractionRange();

        Vec3d rayEnd = cameraPos.add(
                rotation.x * blockInteractionRange,
                rotation.y * blockInteractionRange,
                rotation.z * blockInteractionRange
        );

        return player.getEntityWorld().raycast(
                new RaycastContext(
                        cameraPos,
                        rayEnd,
                        RaycastContext.ShapeType.OUTLINE,
                        RaycastContext.FluidHandling.NONE,
                        player
                )
        );
    }

    static int getValue(PlayerEntity player, ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> type) {
        MutableFloat mineRadius = new MutableFloat();

        EnchantmentHelper.forEachEnchantment(
                player.getMainHandStack(),
                (enchantment, level) -> modifyValue(
                        type,
                        enchantment.value(),
                        level,
                        mineRadius
                )
        );

        return Math.max(0, mineRadius.intValue());
    }

    private static void modifyValue(
            ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> type,
            Enchantment enchantment,
            int level,
            MutableFloat repairWithExperience
    ) {
        for (EnchantmentEffectEntry<EnchantmentValueEffect> enchantmentEffectEntry : enchantment.getEffect(type)) {
            repairWithExperience.setValue(enchantmentEffectEntry.effect().apply(
                    level,
                    Random.create(),
                    repairWithExperience.getValue()
            ));
        }
    }

    static boolean canHarvest(PlayerEntity player, BlockPos pos) {
        World world = player.getEntityWorld();
        BlockState state = world.getBlockState(pos);
        ItemStack stack = player.getMainHandStack();

        if (stack.shouldBreak() || stack.willBreakNextUse()) {
            return false;
        }

        return !state.isAir() && player.canHarvest(state) && world.getWorldBorder().contains(pos);
    }

    static boolean attemptBreak(World world, BlockPos pos, PlayerEntity player) {
        if (!player.isSneaking() && canHarvest(player, pos)) {
            BlockBreaker.performBreak(world, player);
            return true;
        }

        return false;
    }

    static void performBreak(World world, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ServerWorld serverWorld = serverPlayer.getEntityWorld();
            ServerPlayerInteractionManager interactionManager = serverPlayer.interactionManager;

            if (serverPlayer.interactionManager instanceof MiningState miningState) {
                miningState.arcaneHand$setIsMining(true);

                List<BlockPos> needsBreaking = new ArrayList<>();

                getBlockBreaker(player).ifPresent(blockBreaker -> needsBreaking.addAll(
                        blockBreaker.collectPossiblePositions(world, player)
                ));

                for (BlockPos pos : needsBreaking) {
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
                                ItemStack stack = player.getMainHandStack();

                                Block.getDroppedStacks(
                                        state,
                                        serverWorld,
                                        pos,
                                        blockEntity,
                                        player,
                                        stack
                                ).forEach(drop -> Block.dropStack(world, pos, drop));

                                state.onStacksDropped(serverWorld, pos, stack, true);
                                stack.postMine(world, state, pos, player);

                                if (player.canHarvest(state)) {
                                    player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
                                    player.addExhaustion(0.005F);
                                }
                            }
                        }
                    }
                }

                miningState.arcaneHand$setIsMining(false);
            }
        }
    }
}
