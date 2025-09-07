package dev.mariany.arcanehand.client.render.entity.state;

import dev.mariany.arcanehand.item.GauntletItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GauntletRenderState {
    public boolean mainHandAlignedRight = false;
    public boolean inMainHand = false;
    public boolean inOffHand = false;
    public boolean mainHandGlinted = false;
    public boolean offHandGlinted = false;
    public int mainHandColor = GauntletItem.DEFAULT_GAUNTLET_COLOR;
    public int offHandColor = GauntletItem.DEFAULT_GAUNTLET_COLOR;
}
