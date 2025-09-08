package dev.mariany.arcanehand.item;

import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.component.AHComponents;
import dev.mariany.arcanehand.component.type.EnchantmentProgressionComponent;
import dev.mariany.arcanehand.item.equipment.AHMaterials;
import dev.mariany.arcanehand.tag.AHTags;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

public class AHItems {
    public static final Item GAUNTLET = register(
            "gauntlet",
            GauntletItem::new,
            AHMaterials.GAUNTLET
                    .createSettings(
                            AHTags.Blocks.GAUNTLET_MINEABLE,
                            3,
                            -2.4F,
                            0
                    )
                    .fireproof()
                    .component(DataComponentTypes.ENCHANTABLE, null)
                    .component(AHComponents.ENCHANTMENT_PROGRESSION, EnchantmentProgressionComponent.DEFAULT)
    );

    private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> itemKey = keyOf(name);
        Item item = factory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    private static RegistryKey<Item> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ITEM, ArcaneHand.id(id));
    }

    public static void bootstrap() {
        ArcaneHand.LOGGER.info("Registering Items for {}", ArcaneHand.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.NETHERITE_HOE, GAUNTLET);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addAfter(Items.MACE, GAUNTLET);
        });

        registerCauldronBehavior();
    }

    private static void registerCauldronBehavior() {
        CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(GAUNTLET, CauldronBehavior::cleanArmor);
    }
}
