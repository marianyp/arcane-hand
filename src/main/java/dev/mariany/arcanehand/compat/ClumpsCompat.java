package dev.mariany.arcanehand.compat;

import com.blamejared.clumps.api.events.ClumpsEvents;
import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.util.ExperienceOrbHelper;
import net.fabricmc.loader.api.FabricLoader;

public class ClumpsCompat {
    private static final String MOD_ID = "clumps";

    public static void bootstrap() {
        if (FabricLoader.getInstance().isModLoaded(MOD_ID)) {
            ArcaneHand.LOGGER.info("Registering Clumps Mod Compatibility");

            ClumpsEvents.VALUE_EVENT.register(valueEvent -> {
                valueEvent.setValue(
                        ExperienceOrbHelper.handleExperienceCollection(
                                valueEvent.getPlayer(),
                                valueEvent.getValue()
                        )
                );

                return null;
            });
        }
    }
}
