package dev.mariany.arcanehand.client.gui.screen.ingame;

import dev.mariany.arcanehand.AHHelpers;
import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.component.enchantment.EnchantmentProgression;
import dev.mariany.arcanehand.screen.ArcaneConsoleScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ArcaneConsoleScreen extends HandledScreen<ArcaneConsoleScreenHandler> {
    private static final Identifier BACKGROUND = ArcaneHand.id("textures/gui/container/arcane_console.png");

    private static final Identifier ENABLED = ArcaneHand.id("container/arcane_console/enabled");

    private static final Identifier OPTION = ArcaneHand.id("container/arcane_console/option");
    private static final Identifier OPTION_HIGHLIGHTED =
            ArcaneHand.id("container/arcane_console/option_highlighted");
    private static final Identifier OPTION_DISABLED =
            ArcaneHand.id("container/arcane_console/option_disabled");

    private static final Identifier SCROLLER = ArcaneHand.id("container/arcane_console/scroller");
    private static final Identifier SCROLLER_DISABLED =
            ArcaneHand.id("container/arcane_console/scroller_disabled");

    private static final String ELLIPSIS = "...";

    private static final int TEXT_COLOR = -9937334;
    private static final int TEXT_COLOR_HIGHLIGHTED = -128;
    private static final int TEXT_COLOR_DISABLED = 0xFF82745C;

    private static final int MAX_DISPLAYED_ENCHANTMENTS = 4;
    private static final int OPTION_WIDTH = 107;
    private static final int OPTION_HEIGHT = 18;
    private static final int ICON_WIDTH = 18;
    private static final int ICON_HEIGHT = 18;
    private static final int TEXT_RIGHT_PADDING = 4;
    private static final int TEXT_LEFT_PADDING = 2;
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int SCROLLER_TRACK_HEIGHT = 72;

    private static final int SCROLLER_TOP_LEFT_X = 156;
    private static final int SCROLLER_TOP_LEFT_Y = 19;

    private static final int OPTIONS_TOP_LEFT_X = 46;
    private static final int OPTIONS_TOP_LEFT_Y = 19;

    private boolean mouseClicked;
    private float scrollAmount;
    private int scrollOffset;

    public ArcaneConsoleScreen(
            ArcaneConsoleScreenHandler handler,
            PlayerInventory inventory,
            Text title
    ) {
        super(handler, inventory, title);
        handler.setContentsChangedListener(this::onInventoryChange);
        this.backgroundHeight = 190;
        this.playerInventoryTitleY = this.playerInventoryTitleY + 24;
    }

    private void onInventoryChange() {
        if (!this.shouldScroll()) {
            this.scrollAmount = 0;
            this.scrollOffset = 0;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int centerX = (this.width - this.backgroundWidth) / 2;
        int centerY = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                centerX,
                centerY,
                0,
                0,
                this.backgroundWidth,
                this.backgroundHeight,
                256,
                256
        );

        this.renderScroller(context, this.x + 156, this.y + 19);

        this.renderOptions(
                context,
                mouseX,
                mouseY,
                this.x + OPTIONS_TOP_LEFT_X,
                this.y + OPTIONS_TOP_LEFT_Y,
                this.scrollOffset + MAX_DISPLAYED_ENCHANTMENTS
        );
    }

    private void renderScroller(DrawContext context, int x, int y) {
        int offset = Math.round(scrollAmount * (SCROLLER_TRACK_HEIGHT - SCROLLER_HEIGHT));

        context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                this.shouldScroll() ? SCROLLER : SCROLLER_DISABLED,
                x,
                y + offset,
                SCROLLER_WIDTH,
                SCROLLER_HEIGHT
        );
    }

    private void renderOptions(DrawContext context, int mouseX, int mouseY, int x, int y, int endIndexExclusive) {
        List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> availableEnchantments =
                this.handler.getSortedAvailableEnchantments();

        this.renderOptionsBackground(context, availableEnchantments, mouseX, mouseY, x, y, endIndexExclusive);
        this.renderOptionsIcon(context, availableEnchantments, x, y, endIndexExclusive);
        this.renderOptionsText(context, availableEnchantments, mouseX, mouseY, x, y, endIndexExclusive);
    }

    private void renderOptionsBackground(
            DrawContext context,
            List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> availableEnchantments,
            int mouseX,
            int mouseY,
            int x,
            int y,
            int endIndexExclusive
    ) {
        final int start = this.scrollOffset;
        final int end = Math.min(endIndexExclusive, availableEnchantments.size());

        for (int i = start; i < end; i++) {
            int row = i - start;
            int placedY = y + row * OPTION_HEIGHT;

            Identifier texture = OPTION_DISABLED;

            if (this.handler.isCompatible(availableEnchantments.get(i).getKey())) {
                texture = isOptionHighlighted(mouseX, mouseY, x, placedY)
                        ? OPTION_HIGHLIGHTED
                        : OPTION;
            }

            context.drawGuiTexture(
                    RenderPipelines.GUI_TEXTURED,
                    texture,
                    x,
                    placedY,
                    OPTION_WIDTH,
                    OPTION_HEIGHT
            );
        }
    }

    private void renderOptionsIcon(
            DrawContext context,
            List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> availableEnchantments,
            int x,
            int y,
            int endIndexExclusive
    ) {
        final int start = this.scrollOffset;
        final int end = Math.min(endIndexExclusive, availableEnchantments.size());

        for (int i = start; i < end; i++) {
            int row = i - start;
            int placedY = y + row * OPTION_HEIGHT;

            if (this.handler.isCompatible(availableEnchantments.get(i).getKey())) {
                if(availableEnchantments.get(i).getValue().isEnabled()) {
                    context.drawGuiTexture(
                            RenderPipelines.GUI_TEXTURED,
                            ENABLED,
                            x,
                            placedY,
                            ICON_WIDTH,
                            ICON_HEIGHT
                    );
                }
            }
        }
    }

    private void renderOptionsText(
            DrawContext context,
            List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> availableEnchantments,
            int mouseX,
            int mouseY,
            int x,
            int y,
            int endIndexExclusive
    ) {
        if (this.client != null && this.client.world != null) {
            this.client.world
                    .getRegistryManager()
                    .getOptional(RegistryKeys.ENCHANTMENT)
                    .ifPresent(enchantmentRegistry -> {
                        final int start = this.scrollOffset;
                        final int end = Math.min(endIndexExclusive, availableEnchantments.size());

                        for (int i = start; i < end; i++) {
                            final int row = i - start;

                            final int optionX = x;
                            final int optionY = y + row * OPTION_HEIGHT;

                            final int textX = optionX + ICON_WIDTH + TEXT_LEFT_PADDING;
                            final int textMaxWidth = Math.max(
                                    0,
                                    OPTION_WIDTH - (ICON_WIDTH + TEXT_LEFT_PADDING) - TEXT_RIGHT_PADDING
                            );

                            RegistryKey<Enchantment> enchantmentKey = availableEnchantments.get(i).getKey();

                            enchantmentRegistry
                                    .getOptional(enchantmentKey)
                                    .ifPresent(enchantment -> {
                                        String raw = enchantment.value().description().getString();
                                        String display = raw;

                                        if (this.textRenderer.getWidth(raw) > textMaxWidth) {
                                            int allowed = textMaxWidth - this.textRenderer.getWidth(ELLIPSIS);

                                            if (allowed > 0) {
                                                display = this.textRenderer.trimToWidth(raw, allowed) + ELLIPSIS;
                                            } else {
                                                display = ELLIPSIS;
                                            }
                                        }

                                        int fontHeight = this.textRenderer.fontHeight;
                                        int textY = (optionY + (OPTION_HEIGHT - fontHeight) / 2) + 1;

                                        int color = TEXT_COLOR_DISABLED;

                                        if (this.handler.isCompatible(enchantmentKey)) {
                                            color = this.isOptionHighlighted(mouseX, mouseY, optionX, optionY)
                                                    ? TEXT_COLOR_HIGHLIGHTED
                                                    : TEXT_COLOR;
                                        }

                                        context.drawText(
                                                this.textRenderer,
                                                display,
                                                textX,
                                                textY,
                                                color,
                                                false
                                        );
                                    });
                        }
                    });
        }
    }

    private boolean isOptionHighlighted(double mouseX, double mouseY, int optionX, int optionY) {
        return AHHelpers.pointInRect(mouseX, mouseY, optionX, optionY, OPTION_WIDTH, OPTION_HEIGHT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        final int optionsX = this.x + OPTIONS_TOP_LEFT_X;
        final int optionsY = this.y + OPTIONS_TOP_LEFT_Y;
        final int scrollerX = this.x + SCROLLER_TOP_LEFT_X;
        final int scrollerY = this.y + SCROLLER_TOP_LEFT_Y;

        this.mouseClicked = false;

        if (this.client != null && this.client.player != null && this.client.interactionManager != null) {
            final int count = this.getAvailableEnchantmentsCount();

            final int start = this.scrollOffset;
            final int end = Math.min(start + MAX_DISPLAYED_ENCHANTMENTS, count);

            for (int i = start; i < end; i++) {
                final int row = i - start;
                final int optionY = optionsY + row * OPTION_HEIGHT;

                if (this.isOptionHighlighted(mouseX, mouseY, optionsX, optionY)) {
                    if (this.handler.onButtonClick(this.client.player, i)) {
                        this.client.interactionManager.clickButton(this.handler.syncId, i);
                        this.client.getSoundManager()
                                   .play(PositionedSoundInstance.master(
                                           SoundEvents.UI_STONECUTTER_SELECT_RECIPE,
                                           1.0F
                                   ));
                    }
                    return true;
                }

                if (
                        AHHelpers.pointInRect(
                                mouseX,
                                mouseY,
                                scrollerX,
                                scrollerY,
                                SCROLLER_WIDTH,
                                SCROLLER_TRACK_HEIGHT
                        )
                ) {
                    this.mouseClicked = true;
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.mouseClicked && this.shouldScroll()) {
            int top = this.y + SCROLLER_HEIGHT - 1;
            float mouseOffsetFromCenter = (float) mouseY - top - ((float) SCROLLER_HEIGHT / 2);
            float scrollableHeight = SCROLLER_TRACK_HEIGHT - SCROLLER_HEIGHT;

            this.scrollAmount = MathHelper.clamp(mouseOffsetFromCenter / scrollableHeight, 0, 1);
            this.scrollOffset = (int) (this.scrollAmount * this.getMaxScroll() + 0.5F);

            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            if (this.shouldScroll()) {
                int maxScroll = this.getMaxScroll();
                float scrollStep = (float) verticalAmount / maxScroll;

                this.scrollAmount = MathHelper.clamp(this.scrollAmount - scrollStep, 0, 1);
                this.scrollOffset = (int) (this.scrollAmount * this.getMaxScroll() + 0.5F);
            }
        }

        return true;
    }

    private boolean shouldScroll() {
        return this.getAvailableEnchantmentsCount() > MAX_DISPLAYED_ENCHANTMENTS;
    }

    private int getMaxScroll() {
        return Math.max(0, this.getAvailableEnchantmentsCount() - MAX_DISPLAYED_ENCHANTMENTS);
    }

    private int getAvailableEnchantmentsCount() {
        return this.handler.getSortedAvailableEnchantments().size();
    }
}
