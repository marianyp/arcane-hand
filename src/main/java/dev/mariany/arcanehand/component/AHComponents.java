package dev.mariany.arcanehand.component;

import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.component.type.EnchantmentProgressionComponent;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class AHComponents {
    public static final ComponentType<EnchantmentProgressionComponent> ENCHANTMENT_PROGRESSION = register(
            "enchantment_progression",
            ComponentType.<EnchantmentProgressionComponent>builder()
                         .codec(EnchantmentProgressionComponent.CODEC)
                         .packetCodec(EnchantmentProgressionComponent.PACKET_CODEC)
                         .cache()
    );

    private AHComponents() {
    }

    private static <T> ComponentType<T> register(String name, ComponentType.Builder<T> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, ArcaneHand.id(name), builder.build());
    }

    public static void bootstrap() {
        ArcaneHand.bootstrapLog("Components");

        ComponentTooltipAppenderRegistry.addBefore(DataComponentTypes.ENCHANTMENTS, ENCHANTMENT_PROGRESSION);
    }
}
