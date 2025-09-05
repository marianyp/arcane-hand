package dev.mariany.arcanehand.mixin;

import dev.mariany.arcanehand.client.render.entity.state.EntityWithGauntletRenderState;
import dev.mariany.arcanehand.client.render.entity.state.GauntletRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements EntityWithGauntletRenderState {
    @Unique
    private final GauntletRenderState gauntletRenderState = new GauntletRenderState();

    @Override
    public GauntletRenderState arcanehand$getGauntletRenderState() {
        return this.gauntletRenderState;
    }
}
