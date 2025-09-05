package dev.mariany.arcanehand.client;

import dev.mariany.arcanehand.client.event.ClientTickHandler;
import dev.mariany.arcanehand.client.gui.screen.ingame.ArcaneConsoleScreen;
import dev.mariany.arcanehand.client.render.entity.feature.GauntletFeatureRenderer;
import dev.mariany.arcanehand.client.render.entity.model.AHModels;
import dev.mariany.arcanehand.screen.AHScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class ArcaneHandClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AHModels.bootstrap();
        registerScreenHandlers();
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(this::registerEntityFeatures);
        ClientTickHandler.registerTickHandler();
    }

    private static void registerScreenHandlers() {
        HandledScreens.register(AHScreenHandlers.ARCANE_CONSOLE, ArcaneConsoleScreen::new);
    }

    private void registerEntityFeatures(
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
        } else if (livingEntityRenderer instanceof PlayerEntityRenderer playerEntityRenderer) {
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
