package dev.mariany.arcanehand.client;

import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.client.event.ClientTickHandler;
import dev.mariany.arcanehand.client.event.FeatureRendererRegistrationHandler;
import dev.mariany.arcanehand.client.event.TooltipComponentHandler;
import dev.mariany.arcanehand.client.gui.screen.ingame.ArcaneConsoleScreen;
import dev.mariany.arcanehand.client.render.entity.model.AHModels;
import dev.mariany.arcanehand.screen.AHScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class ArcaneHandClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AHModels.bootstrap();
        registerScreenHandlers();
        FeatureRendererRegistrationHandler.bootstrap();
        TooltipComponentHandler.bootstrap();
        ClientTickHandler.bootstrap();
        InvalidateRenderStateCallback.EVENT.register(ArcaneHand::reloadConfig);
    }

    private static void registerScreenHandlers() {
        HandledScreens.register(AHScreenHandlers.ARCANE_CONSOLE, ArcaneConsoleScreen::new);
    }
}
