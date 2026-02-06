package dev.mariany.arcanehand.client.event;

import dev.mariany.arcanehand.client.render.entity.feature.GauntletFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public final class FeatureRendererRegistrationHandler {
    private FeatureRendererRegistrationHandler() {
    }

    public static void bootstrap() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
                FeatureRendererRegistrationHandler::registerEntityFeatures
        );
    }

    private static void registerEntityFeatures(
            EntityType<? extends LivingEntity> entityType,
            LivingEntityRenderer<?, ?, ?> livingEntityRenderer,
            LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper,
            EntityRendererFactory.Context context
    ) {
        LoadedEntityModels models = context.getEntityModels();

        if (livingEntityRenderer instanceof ArmorStandEntityRenderer armorStandEntityRenderer) {
            registrationHelper.register(
                    new GauntletFeatureRenderer<>(armorStandEntityRenderer, models)
            );
        } else if (livingEntityRenderer instanceof PlayerEntityRenderer<?> playerEntityRenderer) {
            registrationHelper.register(
                    new GauntletFeatureRenderer<>(playerEntityRenderer, models)
            );
        } else if (livingEntityRenderer instanceof BipedEntityRenderer<?, ?, ?> bipedEntityRenderer) {
            registrationHelper.register(
                    new GauntletFeatureRenderer<>(bipedEntityRenderer, models)
            );
        }
    }
}
