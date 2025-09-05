package dev.mariany.arcanehand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.mariany.arcanehand.item.AHItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapOperation(
            method = "onEquipStack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/Entity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V"
            )
    )
    private void wrapOnEquipStack(
            World world,
            @Nullable Entity entity,
            double x,
            double y,
            double z,
            RegistryEntry<SoundEvent> soundEvent,
            SoundCategory soundCategory,
            float volume,
            float pitch,
            long seed,
            Operation<Void> original,
            @Local(index = 3, argsOnly = true) ItemStack newStack
    ) {
        if (newStack.isOf(AHItems.GAUNTLET)) {
            return;
        }

        original.call(world, entity, x, y, z, soundEvent, soundCategory, volume, pitch, seed);
    }
}
