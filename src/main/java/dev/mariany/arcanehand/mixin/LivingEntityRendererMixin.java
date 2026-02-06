package dev.mariany.arcanehand.mixin;

import dev.mariany.arcanehand.client.render.entity.state.EntityWithGauntletRenderState;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(
            method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
            at = @At(value = "TAIL")
    )
    public <T extends LivingEntity, S extends LivingEntityRenderState> void updateRenderState(
            T livingEntity,
            S livingEntityRenderState,
            float scale,
            CallbackInfo ci
    ) {
        if (livingEntityRenderState instanceof EntityWithGauntletRenderState entityWithGauntletRenderState) {
            entityWithGauntletRenderState.updateRenderState(livingEntity);
        }
    }
}
