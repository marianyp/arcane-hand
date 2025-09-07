package dev.mariany.arcanehand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.arcanehand.item.GauntletItem;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @WrapOperation(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;set(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    public <T> T updateResult(ItemStack stack, ComponentType<T> type, @Nullable T value, Operation<T> original) {
        if (GauntletItem.isGauntlet(stack)) {
            return original.call(stack, type, 0);
        }

        return original.call(stack, type, value);
    }
}
