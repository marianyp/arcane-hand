package dev.mariany.arcanehand.compat;

import com.blamejared.clumps.api.events.ClumpsEvents;
import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.item.GauntletItem;
import net.fabricmc.loader.api.FabricLoader;

public final class ClumpsCompat {
    private static final String MOD_ID = "clumps";

    private ClumpsCompat() {
    }

    public static void bootstrap() {
        if (FabricLoader.getInstance().isModLoaded(MOD_ID)) {
            ArcaneHand.bootstrapLog("Clumps Mod Compatibility");

            ClumpsEvents.VALUE_EVENT.register(valueEvent -> {
                valueEvent.setValue(
                        GauntletItem.handleExperienceCollection(valueEvent.getPlayer(), valueEvent.getValue())
                );

                return null;
            });
        }
    }
}
