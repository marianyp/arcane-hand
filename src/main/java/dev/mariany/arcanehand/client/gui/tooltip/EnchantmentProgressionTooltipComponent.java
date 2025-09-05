package dev.mariany.arcanehand.client.gui.tooltip;

import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.component.enchantment.EnchantmentProgressionComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EnchantmentProgressionTooltipComponent implements TooltipComponent {
    private static final Identifier ENCHANTMENT_PROGRESS_BAR_BORDER_TEXTURE =
            ArcaneHand.id("container/enchantment/enchantment_progressbar_border");
    private static final Identifier ENCHANTMENT_PROGRESS_BAR_FILL_TEXTURE =
            ArcaneHand.id("container/enchantment/enchantment_progressbar_fill");

    protected final EnchantmentProgressionComponent enchantmentProgression;

    public EnchantmentProgressionTooltipComponent(EnchantmentProgressionComponent enchantmentProgression) {
        this.enchantmentProgression = enchantmentProgression;
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        return 0;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 96;
    }
}
