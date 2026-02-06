package dev.mariany.arcanehand.stat;

import dev.mariany.arcanehand.ArcaneHand;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

public class AHStats {
    public static final Identifier INTERACT_WITH_ARCANE_CONSOLE = register(
            "interact_with_arcane_console",
            StatFormatter.DEFAULT
    );

    private AHStats() {
    }

    private static Identifier register(String id, StatFormatter formatter) {
        Identifier identifier = ArcaneHand.id(id);
        Registry.register(Registries.CUSTOM_STAT, id, identifier);
        Stats.CUSTOM.getOrCreateStat(identifier, formatter);
        return identifier;
    }

    public static void bootstrap() {
        ArcaneHand.bootstrapLog("Stats");
    }
}
