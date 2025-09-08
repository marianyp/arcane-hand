package dev.mariany.arcanehand.component;

import dev.mariany.arcanehand.ArcaneHand;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;

import java.util.function.UnaryOperator;

public interface AHEnchantmentEffectComponents {
    ComponentType<Unit> SMELT_DROPS = register("smelt_drops", builder -> builder.codec(Unit.CODEC));

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(
                Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
                ArcaneHand.id(name),
                builderOperator.apply(ComponentType.builder()).build()
        );
    }

    static void bootstrap() {
        ArcaneHand.LOGGER.info("Registering Enchantment Effect Components for {}", ArcaneHand.MOD_ID);
    }
}
