package dev.mariany.arcanehand.mixin;

import dev.mariany.arcanehand.logic.BlockBreaker;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(at = @At("HEAD"), method = "drawBlockOutline", cancellable = true)
    private void injectDrawBlockOutline(
            MatrixStack matrices,
            VertexConsumer vertexConsumer,
            Entity entity,
            double cameraX,
            double cameraY,
            double cameraZ,
            BlockPos pos,
            BlockState state,
            int color,
            CallbackInfo ci
    ) {
        ClientPlayerEntity player = this.client.player;
        ClientWorld world = this.client.world;

        if (player != null && world != null) {
            BlockBreaker.getBlockBreaker(player).ifPresent(blockBreaker -> {
                if (!player.isSneaking() && client.crosshairTarget instanceof BlockHitResult crosshairTarget) {
                    BlockPos crosshairPos = crosshairTarget.getBlockPos();

                    if (BlockBreaker.canHarvest(player, crosshairPos)) {
                        List<BlockPos> positions = blockBreaker.collectPositions(world, player);
                        List<VoxelShape> outlineShapes = new ArrayList<>();
                        outlineShapes.add(VoxelShapes.empty());

                        for (BlockPos position : positions) {
                            if (BlockBreaker.canHarvest(player, crosshairPos)) {
                                BlockPos diffPos = position.subtract(crosshairPos);
                                BlockState offsetShape = world.getBlockState(position);

                                if (!offsetShape.isAir()) {
                                    outlineShapes.set(
                                            0,
                                            VoxelShapes.union(
                                                    outlineShapes.getFirst(),
                                                    VoxelShapes.fullCube().offset(
                                                            diffPos.getX(),
                                                            diffPos.getY(),
                                                            diffPos.getZ()
                                                    )
                                            )
                                    );
                                }
                            }
                        }

                        outlineShapes.forEach(shape -> VertexRendering.drawOutline(
                                matrices,
                                vertexConsumer,
                                shape,
                                (double) crosshairPos.getX() - cameraX,
                                (double) crosshairPos.getY() - cameraY,
                                (double) crosshairPos.getZ() - cameraZ,
                                color
                        ));

                        // Cancel the 1x1 hitbox that would normally render
                        ci.cancel();
                    }
                }
            });
        }
    }
}


