package dev.mariany.arcanehand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mariany.arcanehand.component.AHEnchantmentEffectComponents;
import dev.mariany.arcanehand.logic.SelfInserting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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

    @WrapOperation(
            method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    private static void wrapDropStacks(
            List<?> instance,
            Consumer<?> consumer,
            Operation<Void> original,
            @Local(index = 0, argsOnly = true) BlockState state,
            @Local(index = 1, argsOnly = true) World world,
            @Local(index = 2, argsOnly = true) BlockPos pos,
            @Local(index = 3, argsOnly = true) @Nullable BlockEntity blockEntity,
            @Local(index = 4, argsOnly = true) @Nullable Entity entity,
            @Local(index = 5, argsOnly = true) ItemStack tool
    ) {
        if (entity instanceof PlayerEntity player) {
            if (EnchantmentHelper.hasAnyEnchantmentsWith(tool, AHEnchantmentEffectComponents.COLLECT)) {
                SelfInserting.insert(world, player, pos, state, blockEntity, tool);
                return;
            }
        }

        original.call(instance, consumer);
    }
}
