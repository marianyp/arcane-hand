package dev.mariany.arcanehand.screen;

import dev.mariany.arcanehand.ArcaneHand;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class AHScreenHandlers {
    public static final ScreenHandlerType<ArcaneConsoleScreenHandler> ARCANE_CONSOLE =
            register("arcane_console", ArcaneConsoleScreenHandler::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(
            String id,
            ScreenHandlerType.Factory<T> factory
    ) {
        return Registry.register(
                Registries.SCREEN_HANDLER,
                ArcaneHand.id(id),
                new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES)
        );
    }

    public static void bootstrap() {
        ArcaneHand.LOGGER.info("Registering Screen Handlers for {}", ArcaneHand.MOD_ID);
    }
}
