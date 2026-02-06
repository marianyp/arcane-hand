package dev.mariany.arcanehand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.arcanehand.client.render.entity.feature.GauntletFeatureRendererHelper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin
        extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {
    public PlayerEntityRendererMixin(
            EntityRendererFactory.Context context,
            PlayerEntityModel entityModel,
            float shadowRadius
    ) {
        super(context, entityModel, shadowRadius);
    }

    @WrapOperation(
            method = "getArmPose(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z")
    )
    private static boolean wrapGetArmPose(
            ItemStack stack, Operation<Boolean> original
    ) {
        if (GauntletFeatureRendererHelper.hasEmptyArmPose(stack)) {
            return true;
        }

        return original.call(stack);
    }

    @Inject(method = "renderRightArm", at = @At("TAIL"))
    private void injectRenderRight(
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light,
            Identifier skinTexture,
            boolean sleeveVisible,
            CallbackInfo ci
    ) {
        PlayerEntityRenderer<?> self = (PlayerEntityRenderer<?>) (Object) this;
        GauntletFeatureRendererHelper.renderRightArm(self, matrices, queue, light);
    }

    @Inject(method = "renderLeftArm", at = @At("TAIL"))
    private void injectRenderLeft(
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light,
            Identifier skinTexture,
            boolean sleeveVisible,
            CallbackInfo ci
    ) {
        PlayerEntityRenderer<?> self = (PlayerEntityRenderer<?>) (Object) this;
        GauntletFeatureRendererHelper.renderLeftArm(self, matrices, queue, light);
    }
}
