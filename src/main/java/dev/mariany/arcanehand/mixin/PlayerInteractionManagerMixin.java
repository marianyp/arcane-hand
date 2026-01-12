package dev.mariany.arcanehand.mixin;

import dev.mariany.arcanehand.logic.BlockBreaker;
import dev.mariany.arcanehand.server.network.MiningState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class PlayerInteractionManagerMixin implements MiningState {
    @Final
    @Shadow
    protected ServerPlayerEntity player;
    @Shadow
    protected ServerWorld world;
    @Unique
    private boolean isMining = false;

    @Inject(
            method = "tryBreakBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/block/BlockState;"
            ),
            cancellable = true
    )
    private void tryBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (BlockBreaker.getBlockBreaker(player).isPresent()) {
            if (this.isMining || BlockBreaker.attemptBreak(world, pos, player)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Override
    public void arcaneHand$setIsMining(boolean mining) {
        this.isMining = mining;
    }
}
