package dev.mariany.arcanehand.item.equipment;

import dev.mariany.arcanehand.tag.AHTags;
import net.minecraft.registry.tag.BlockTags;

public class AHMaterials {
    public static final DynamicMaterial GAUNTLET = new DynamicMaterial(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
            AHTags.Items.GAUNTLET_TOOL_MATERIALS,
            3,
            4F,
            0.1F,
            9F,
            3,
            1561,
            15
    );
}
