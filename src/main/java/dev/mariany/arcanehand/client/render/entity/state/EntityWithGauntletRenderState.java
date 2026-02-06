package dev.mariany.arcanehand.client.render.entity.state;

import dev.mariany.arcanehand.item.GauntletItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public interface EntityWithGauntletRenderState {
    GauntletRenderState arcanehand$getGauntletRenderState();

    default void updateRenderState(LivingEntity livingEntity) {
        ItemStack mainHandStack = livingEntity.getMainHandStack();
        ItemStack offHandStack = livingEntity.getOffHandStack();

        GauntletRenderState gauntletRenderState = this.arcanehand$getGauntletRenderState();

        gauntletRenderState.mainHandAlignedRight = livingEntity.getMainArm().equals(Arm.RIGHT);
        gauntletRenderState.inMainHand = GauntletItem.isGauntlet(mainHandStack);
        gauntletRenderState.inOffHand = GauntletItem.isGauntlet(offHandStack);
        gauntletRenderState.mainHandGlinted = mainHandStack.hasGlint();
        gauntletRenderState.offHandGlinted = offHandStack.hasGlint();
        gauntletRenderState.mainHandColor = GauntletItem.getColor(mainHandStack);
        gauntletRenderState.offHandColor = GauntletItem.getColor(offHandStack);
    }
}
