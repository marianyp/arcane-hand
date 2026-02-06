package dev.mariany.arcanehand.enchantment.logic.drop;

import dev.mariany.arcanehand.component.AHEnchantmentEffectComponents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DropSmelter {
    private DropSmelter() {
    }

    public static List<ItemStack> smeltDrops(ServerWorld world, ItemStack tool, List<ItemStack> drops) {
        List<ItemStack> smeltedDrops = new ArrayList<>();

        if (!canSmelt(tool)) {
            return drops;
        }

        for (ItemStack drop : drops) {
            SingleStackRecipeInput input = new SingleStackRecipeInput(drop);

            Optional<RecipeEntry<SmeltingRecipe>> recipe = world.getRecipeManager().getFirstMatch(
                    RecipeType.SMELTING,
                    input,
                    world
            );

            if (recipe.isPresent()) {
                ItemStack smelted = recipe.get().value().craft(input, world.getRegistryManager()).copy();
                smelted.setCount(drop.getCount());
                smeltedDrops.add(smelted);
            } else {
                smeltedDrops.add(drop);
            }
        }

        return smeltedDrops;
    }

    private static boolean canSmelt(ItemStack stack) {
        return EnchantmentHelper.hasAnyEnchantmentsWith(stack, AHEnchantmentEffectComponents.SMELT_DROPS);
    }
}
