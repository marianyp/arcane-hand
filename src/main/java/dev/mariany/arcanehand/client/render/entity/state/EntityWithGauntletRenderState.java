package dev.mariany.arcanehand.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface EntityWithGauntletRenderState {
    GauntletRenderState arcanehand$getGauntletRenderState();
}
