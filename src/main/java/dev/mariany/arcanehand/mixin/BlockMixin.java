package dev.mariany.arcanehand.mixin;

import dev.mariany.arcanehand.component.AHEnchantmentEffectComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(
            method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private static void injectDropStacks(
            BlockState state,
            ServerWorld world,
            BlockPos pos,
            @Nullable BlockEntity blockEntity,
            @Nullable Entity entity,
            ItemStack tool,
            CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        List<ItemStack> drops = new ArrayList<>();
        List<ItemStack> originalDrops = cir.getReturnValue();

        if (!EnchantmentHelper.hasAnyEnchantmentsWith(tool, AHEnchantmentEffectComponents.SMELT_DROPS)) {
            cir.setReturnValue(originalDrops);
            return;
        }

        for (ItemStack drop : originalDrops) {
            SingleStackRecipeInput input = new SingleStackRecipeInput(drop);

            Optional<RecipeEntry<SmeltingRecipe>> recipe = world.getRecipeManager().getFirstMatch(
                    RecipeType.SMELTING,
                    input,
                    world
            );

            if (recipe.isPresent()) {
                ItemStack smelted = recipe.get().value().craft(input, world.getRegistryManager()).copy();
                smelted.setCount(drop.getCount());
                drops.add(smelted);
            } else {
                drops.add(drop);
            }
        }

        cir.setReturnValue(drops);
    }
}
