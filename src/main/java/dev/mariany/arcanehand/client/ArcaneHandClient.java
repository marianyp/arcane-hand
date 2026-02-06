package dev.mariany.arcanehand.client;

import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.client.event.FeatureRendererRegistrationHandler;
import dev.mariany.arcanehand.client.event.TooltipComponentHandler;
import dev.mariany.arcanehand.client.gui.screen.ingame.ArcaneConsoleScreen;
import dev.mariany.arcanehand.client.render.entity.model.AHModels;
import dev.mariany.arcanehand.client.config.AHClientConfig;
import dev.mariany.arcanehand.config.ConfigHandler;
import dev.mariany.arcanehand.screen.AHScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class ArcaneHandClient implements ClientModInitializer {
    private static final ConfigHandler<AHClientConfig> CONFIG_HANDLER = new ConfigHandler<>(
            ArcaneHand.MOD_ID + "-client",
            new AHClientConfig()
    );

    private static final GauntletSoundManager GAUNTLET_SOUND_MANAGER = new GauntletSoundManager();

    @Override
    public void onInitializeClient() {
        reloadConfig();

        AHModels.bootstrap();
        FeatureRendererRegistrationHandler.bootstrap();
        TooltipComponentHandler.bootstrap();
        GAUNTLET_SOUND_MANAGER.bootstrap();

        registerScreenHandlers();
        registerInvalidateRenderStateHandler();
    }

    public static AHClientConfig getConfig() {
        return CONFIG_HANDLER.getConfig();
    }

    private static void reloadConfig() {
        CONFIG_HANDLER.loadConfig();
    }

    private static void registerScreenHandlers() {
        HandledScreens.register(AHScreenHandlers.ARCANE_CONSOLE, ArcaneConsoleScreen::new);
    }

    private static void registerInvalidateRenderStateHandler() {
        InvalidateRenderStateCallback.EVENT.register(ArcaneHandClient::reloadConfig);
    }
}
