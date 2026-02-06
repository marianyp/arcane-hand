package dev.mariany.arcanehand.advancement.criterion;

import dev.mariany.arcanehand.ArcaneHand;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class AHCriteria {
    public static final TickCriterion GAUNTLET_LEVELED_UP = register("gauntlet_leveled_up", new TickCriterion());

    private AHCriteria() {
    }

    public static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, ArcaneHand.id(name), criterion);
    }

    public static void bootstrap() {
        ArcaneHand.bootstrapLog("Criteria");
    }
}
