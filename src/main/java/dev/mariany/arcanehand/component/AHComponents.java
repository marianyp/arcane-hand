package dev.mariany.arcanehand.component;

import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.component.enchantment.EnchantmentProgressionComponent;
import net.fabricmc.fabric.impl.item.ComponentTooltipAppenderRegistryImpl;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class AHComponents {
    public static ComponentType<EnchantmentProgressionComponent> ENCHANTMENT_PROGRESSION = register(
            "enchantment_progression",
            ComponentType.<EnchantmentProgressionComponent>builder()
                         .codec(EnchantmentProgressionComponent.CODEC)
                         .packetCodec(EnchantmentProgressionComponent.PACKET_CODEC)
                         .cache()
    );

    private static <T> ComponentType<T> register(String name, ComponentType.Builder<T> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, ArcaneHand.id(name), builder.build());
    }

    public static void bootstrap() {
        ArcaneHand.LOGGER.info("Registering Components for {}", ArcaneHand.MOD_ID);

        ComponentTooltipAppenderRegistryImpl.addBefore(DataComponentTypes.ENCHANTMENTS, ENCHANTMENT_PROGRESSION);
    }
}
