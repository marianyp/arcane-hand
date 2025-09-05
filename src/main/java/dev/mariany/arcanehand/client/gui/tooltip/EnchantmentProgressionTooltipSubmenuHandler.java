package dev.mariany.arcanehand.client.gui.tooltip;

import dev.mariany.arcanehand.component.AHComponents;
import dev.mariany.arcanehand.item.GauntletItem;
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler;
import net.minecraft.client.input.Scroller;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.joml.Vector2i;

public class EnchantmentProgressionTooltipSubmenuHandler implements TooltipSubmenuHandler {
    private final Scroller scroller = new Scroller();

    @Override
    public boolean isApplicableTo(Slot slot) {
        return slot.getStack().contains(AHComponents.ENCHANTMENT_PROGRESSION);
    }

    @Override
    public boolean onScroll(double horizontal, double vertical, int slotId, ItemStack item) {
        int enchantmentCount = GauntletItem.getEnchantmentCount(item);

        if (enchantmentCount == 0) {
            return false;
        }

        Vector2i scrollVector = this.scroller.update(horizontal, vertical);
        int scrollDirection = scrollVector.y == 0 ? -scrollVector.x : scrollVector.y;

        if (scrollDirection != 0) {
            int previousEnchantmentIndex = GauntletItem.getSelectedEnchantmentIndex(item);
            int selectedEnchantmentIndex = Scroller.scrollCycling(
                    scrollDirection,
                    previousEnchantmentIndex,
                    enchantmentCount
            );

            if (previousEnchantmentIndex != selectedEnchantmentIndex) {
                this.update(item, selectedEnchantmentIndex);
            }
        }

        return true;
    }

    @Override
    public void reset(Slot slot) {
        this.reset(slot.getStack());
    }

    @Override
    public void onMouseClick(Slot slot, SlotActionType actionType) {
        if (actionType == SlotActionType.QUICK_MOVE || actionType == SlotActionType.SWAP) {
            this.reset(slot.getStack());
        }
    }

    public void reset(ItemStack stack) {
        this.update(stack, 0);
    }

    private void update(ItemStack stack, int selectedEnchantmentIndex) {
        if (selectedEnchantmentIndex < GauntletItem.getEnchantmentCount(stack)) {
            GauntletItem.setSelectedEnchantmentIndex(stack, selectedEnchantmentIndex);
        }
    }
}
