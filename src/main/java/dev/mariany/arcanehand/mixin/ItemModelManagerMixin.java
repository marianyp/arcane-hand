package dev.mariany.arcanehand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mariany.arcanehand.item.GauntletItem;
import dev.mariany.arcanehand.util.AHHelper;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemModelManager.class)
public class ItemModelManagerMixin {
    @WrapOperation(
            method = "clearAndUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"
            )
    )
    public boolean wrapClearAndUpdate(
            ItemStack stack,
            Operation<Boolean> original,
            @Local(index = 3, argsOnly = true) ItemDisplayContext displayContext
    ) {
        boolean thirdPerson = AHHelper.isThirdPerson(displayContext);
        boolean gauntlet = GauntletItem.isGauntlet(stack);

        if (thirdPerson && gauntlet) {
            return true;
        }

        return original.call(stack);
    }
}
