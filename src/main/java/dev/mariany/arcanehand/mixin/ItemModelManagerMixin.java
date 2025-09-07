package dev.mariany.arcanehand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.arcanehand.item.GauntletItem;
import dev.mariany.arcanehand.util.AHHelper;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemModelManager.class)
public class ItemModelManagerMixin {
    @WrapOperation(
            method = "clearAndUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/item/ItemModelManager;update(Lnet/minecraft/client/render/item/ItemRenderState;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)V"
            )
    )
    public void wrapClearAndUpdate(
            ItemModelManager itemModelManager,
            ItemRenderState renderState,
            ItemStack stack,
            ItemDisplayContext displayContext,
            World world,
            LivingEntity entity,
            int seed,
            Operation<Void> original
    ) {
        if (AHHelper.isThirdPerson(displayContext) && GauntletItem.isGauntlet(stack)) {
            stack = Items.AIR.getDefaultStack();
        }

        original.call(
                itemModelManager,
                renderState,
                stack,
                displayContext,
                world,
                entity,
                seed
        );
    }
}
