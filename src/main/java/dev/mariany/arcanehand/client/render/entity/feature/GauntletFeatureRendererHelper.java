package dev.mariany.arcanehand.client.render.entity.feature;

import dev.mariany.arcanehand.item.GauntletItem;
import dev.mariany.arcanehand.mixin.accessor.LivingEntityRendererAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class GauntletFeatureRendererHelper {
    private GauntletFeatureRendererHelper() {
    }

    public static boolean hasEmptyArmPose(ItemStack stack) {
        return GauntletItem.isGauntlet(stack);
    }

    public static void renderRightArm(
            PlayerEntityRenderer<?> playerEntityRenderer,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light
    ) {
        renderGauntletFeature(playerEntityRenderer, matrices, queue, light, Arm.RIGHT);
    }

    public static void renderLeftArm(
            PlayerEntityRenderer<?> playerEntityRenderer,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light
    ) {
        renderGauntletFeature(playerEntityRenderer, matrices, queue, light, Arm.LEFT);
    }

    private static void renderGauntletFeature(
            PlayerEntityRenderer<?> renderer,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light,
            Arm arm
    ) {
        AbstractClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null) {
            ItemStack itemStack = player.getMainArm() == arm ? player.getMainHandStack() : player.getOffHandStack();

            if (GauntletItem.isGauntlet(itemStack)) {
                List<FeatureRenderer<?, ?>> features = ((LivingEntityRendererAccessor) renderer).arcanehand$features();

                for (FeatureRenderer<?, ?> feature : features) {
                    if (feature instanceof GauntletFeatureRenderer<?, ?> gauntletFeatureRenderer) {
                        gauntletFeatureRenderer.renderFirstPerson(
                                matrices,
                                queue,
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
