package dev.mariany.arcanehand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.arcanehand.item.GauntletItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
    @Shadow
    protected abstract void renderArmHoldingItem(
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light,
            float equipProgress,
            float swingProgress,
            Arm arm
    );

    @Inject(
            method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectRenderItem(
            LivingEntity entity,
            ItemStack stack,
            ItemDisplayContext renderMode,
            MatrixStack matrices,
            OrderedRenderCommandQueue orderedRenderCommandQueue,
            int light,
            CallbackInfo ci
    ) {
        if (GauntletItem.isGauntlet(stack)) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "renderFirstPersonItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0)
    )
    private boolean wrapIsEmpty(ItemStack stack, Operation<Boolean> original) {
        return original.call(stack) || GauntletItem.isGauntlet(stack);
    }

    @Inject(
            method = "renderFirstPersonItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingSpyglass()Z",
                    shift = At.Shift.AFTER
            )
    )
    private void injectGauntletRender(
            AbstractClientPlayerEntity player,
            float tickProgress,
            float pitch,
            Hand hand,
            float swingProgress,
            ItemStack item,
            float equipProgress,
            MatrixStack matrices,
            OrderedRenderCommandQueue orderedRenderCommandQueue,
            int light,
            CallbackInfo ci
    ) {
        if (GauntletItem.isGauntlet(player.getStackInHand(hand)) && !player.isInvisible() && hand == Hand.OFF_HAND) {
            this.renderArmHoldingItem(
                    matrices,
                    orderedRenderCommandQueue,
                    light,
                    equipProgress,
                    swingProgress,
                    player.getMainArm().getOpposite()
            );
        }
    }
}
