package dev.mariany.arcanehand.client.render.entity.state;

import dev.mariany.arcanehand.AHHelpers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GauntletRenderState {
    public boolean mainHandAlignedRight = false;
    public boolean inMainHand = false;
    public boolean inOffHand = false;
    public boolean mainHandGlinted = false;
    public boolean offHandGlinted = false;
    public int mainHandColor = AHHelpers.DEFAULT_GAUNTLET_COLOR;
    public int offHandColor = AHHelpers.DEFAULT_GAUNTLET_COLOR;
}
