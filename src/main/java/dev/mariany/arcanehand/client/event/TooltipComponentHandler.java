package dev.mariany.arcanehand.client.event;

import dev.mariany.arcanehand.client.gui.tooltip.EnchantmentProgressionTooltipComponent;
import dev.mariany.arcanehand.component.type.EnchantmentProgressionComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;

@Environment(EnvType.CLIENT)
public class TooltipComponentHandler {
    public static void register() {
        TooltipComponentCallback.EVENT.register(TooltipComponentHandler::getComponent);
    }

    private static TooltipComponent getComponent(TooltipData tooltipData) {
        if (tooltipData instanceof EnchantmentProgressionComponent enchantmentProgression) {
            return new EnchantmentProgressionTooltipComponent(enchantmentProgression, true);
        }

        return null;
    }
}
