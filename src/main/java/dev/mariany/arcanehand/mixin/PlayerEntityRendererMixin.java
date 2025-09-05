package dev.mariany.arcanehand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.arcanehand.AHHelpers;
import dev.mariany.arcanehand.client.render.entity.feature.GauntletFeatureRenderer;
import dev.mariany.arcanehand.client.render.entity.state.EntityWithGauntletRenderState;
import dev.mariany.arcanehand.client.render.entity.state.GauntletRenderState;
import dev.mariany.arcanehand.mixin.accessor.LivingEntityRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin
        extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {
    public PlayerEntityRendererMixin(EntityRendererFactory.Context context, PlayerEntityModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @WrapOperation(
            method = "getArmPose(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z")
    )
    private static boolean wrapGetArmPose(
            ItemStack stack, Operation<Boolean> original
    ) {
        if (AHHelpers.isGauntlet(stack)) {
            return true;
        }

        return original.call(stack);
    }

    @Inject(
            method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
            at = @At(value = "TAIL")
    )
    public void injectUpdateRenderState(
            AbstractClientPlayerEntity player,
            PlayerEntityRenderState playerEntityRenderState,
            float delta,
            CallbackInfo ci
    ) {
        if (playerEntityRenderState instanceof EntityWithGauntletRenderState entityWithGauntletRenderState) {
            GauntletRenderState gauntletRenderState = entityWithGauntletRenderState.arcanehand$getGauntletRenderState();

            ItemStack mainHandStack = player.getMainHandStack();
            ItemStack offHandStack = player.getOffHandStack();

            gauntletRenderState.mainHandAlignedRight = player.getMainArm().equals(Arm.RIGHT);
            gauntletRenderState.inMainHand = AHHelpers.isGauntlet(mainHandStack);
            gauntletRenderState.inOffHand = AHHelpers.isGauntlet(offHandStack);
            gauntletRenderState.mainHandGlinted = mainHandStack.hasGlint();
            gauntletRenderState.offHandGlinted = offHandStack.hasGlint();
            gauntletRenderState.mainHandColor = AHHelpers.getGauntletColor(mainHandStack);
            gauntletRenderState.offHandColor = AHHelpers.getGauntletColor(offHandStack);
        }
    }

    @Inject(method = "renderRightArm", at = @At("TAIL"))
    private void injectRenderRight(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            Identifier skinTexture,
            boolean sleeveVisible,
            CallbackInfo ci
    ) {
        PlayerEntityRenderer self = (PlayerEntityRenderer) (Object) this;
        renderGauntletFeature(self, matrices, vertexConsumers, light, Arm.RIGHT);
    }

    @Inject(method = "renderLeftArm", at = @At("TAIL"))
    private void injectRenderLeft(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            Identifier skinTexture,
            boolean sleeveVisible,
            CallbackInfo ci
    ) {
        PlayerEntityRenderer self = (PlayerEntityRenderer) (Object) this;
        renderGauntletFeature(self, matrices, vertexConsumers, light, Arm.LEFT);
    }

    @Unique
    private void renderGauntletFeature(
            PlayerEntityRenderer renderer,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            Arm arm
    ) {
        AbstractClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null) {
            ItemStack itemStack = player.getMainArm() == arm ? player.getMainHandStack() : player.getOffHandStack();

            if (AHHelpers.isGauntlet(itemStack)) {
                List<FeatureRenderer<?, ?>> features = ((LivingEntityRendererAccessor) renderer).arcanehand$features();

                for (FeatureRenderer<?, ?> feature : features) {
                    if (feature instanceof GauntletFeatureRenderer<?, ?> gauntletFeatureRenderer) {
                        gauntletFeatureRenderer.renderFirstPerson(
                                matrices,
                                vertexConsumers,
                                itemStack,
                                light,
                                arm
                        );
                        break;
                    }
                }
            }
        }
    }
}
