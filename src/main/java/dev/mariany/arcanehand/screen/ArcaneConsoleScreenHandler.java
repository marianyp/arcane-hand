package dev.mariany.arcanehand.screen;

import com.google.common.collect.ImmutableMap;
import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.block.AHBlocks;
import dev.mariany.arcanehand.component.AHComponents;
import dev.mariany.arcanehand.enchantment.EnchantmentProgression;
import dev.mariany.arcanehand.component.type.EnchantmentProgressionComponent;
import dev.mariany.arcanehand.enchantment.EnchantmentProgressionState;
import dev.mariany.arcanehand.enchantment.EnchantmentEntry;
import dev.mariany.arcanehand.item.GauntletItem;
import dev.mariany.arcanehand.tag.AHTags;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.*;

public class ArcaneConsoleScreenHandler extends ScreenHandler {
    private static final Identifier EMPTY_GAUNTLET_SLOT_TEXTURE =
            ArcaneHand.id("container/arcane_console/gauntlet");

    private final ScreenHandlerContext context;
    private final World world;
    private final Slot inputSlot;
    private final Map<RegistryKey<Enchantment>, EnchantmentProgression> availableEnchantments = new HashMap<>();
    private final Inventory inventory = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            ArcaneConsoleScreenHandler.this.onContentChanged(this);
            ArcaneConsoleScreenHandler.this.contentsChangedListener.run();
        }
    };

    private ItemStack inputStack = ItemStack.EMPTY;
    private Runnable contentsChangedListener = () -> {
    };

    public ArcaneConsoleScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    public ArcaneConsoleScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(AHScreenHandlers.ARCANE_CONSOLE, syncId);
        this.context = context;
        this.world = playerInventory.player.getEntityWorld();
        this.inputSlot = this.addSlot(new Slot(this.inventory, 0, 17, 47) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isIn(AHTags.Items.ARCANE_CONSOLE_MODIFIABLE);
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public Identifier getBackgroundSprite() {
                return EMPTY_GAUNTLET_SLOT_TEXTURE;
            }
        });
        this.addPlayerSlots(playerInventory, 8, 108);
    }

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }

    public ImmutableMap<RegistryKey<Enchantment>, EnchantmentProgression> getStackEnchantments() {
        return GauntletItem.getEnchantments(this.inputStack);
    }

    public boolean isCompatible(RegistryKey<Enchantment> candidate) {
        Map<RegistryKey<Enchantment>, EnchantmentProgression> existing = new HashMap<>(getStackEnchantments());

        existing.remove(candidate);

        return this.isCompatible(existing, candidate);
    }

    public boolean isCompatible(
            Map<RegistryKey<Enchantment>, EnchantmentProgression> existing,
            RegistryKey<Enchantment> candidate
    ) {
        return this.world
                .getRegistryManager()
                .getOptional(RegistryKeys.ENCHANTMENT)
                .map(enchantmentRegistry -> {
                    List<RegistryEntry<Enchantment>> enchantments = existing
                            .entrySet()
                            .stream()
                            .map(entry ->
                                         (RegistryEntry<Enchantment>) (entry.getValue().isEnabled() ?
                                                 enchantmentRegistry
                                                         .getOptional(entry.getKey())
                                                         .orElse(null) : null)
                            )
                            .filter(Objects::nonNull)
                            .toList();

                    return enchantmentRegistry
                            .getOptional(candidate)
                            .map(enchantment -> EnchantmentHelper.isCompatible(
                                         enchantments,
                                         enchantment
                                 )
                            )
                            .orElse(false);
                })
                .orElse(false);
    }

    public List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> getSortedAvailableEnchantments() {
        return EnchantmentProgressionComponent.getSortedEntries(
                this.world.getRegistryManager(),
                this.getAvailableEnchantments()
        );
    }

    private Map<RegistryKey<Enchantment>, EnchantmentProgression> getAvailableEnchantments() {
        return Map.copyOf(this.availableEnchantments);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, AHBlocks.ARCANE_CONSOLE);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> this.dropInventory(player, this.inventory));
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> sortedAvailableEnchantments =
                this.getSortedAvailableEnchantments();

        if (sortedAvailableEnchantments.size() > id) {
            Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry =
                    sortedAvailableEnchantments.get(id);

            if (this.isCompatible(entry.getKey())) {
                ItemStack stack = this.inputStack.copy();

                GauntletItem.applyProgress(
                        player.getRegistryManager(),
                        entry.getKey(),
                        entry.getValue().withToggledState(),
                        stack
                );

                this.inputSlot.setStack(stack);

                return true;
            }
        }

        return false;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;

        Slot slot = this.slots.get(slotIndex);

        if (slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            result = slotStack.copy();

            if (slotIndex == 0) {
                if (!this.insertItem(slotStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickTransfer(slotStack, result);
            } else if (
                    !this.inputSlot.hasStack() && this.inputSlot.canInsert(slotStack) && slotStack.getCount() == 1
            ) {
                if (!this.insertItem(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 1 && slotIndex < 28) {
                if (!this.insertItem(slotStack, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 28 && slotIndex < 37) {
                if (!this.insertItem(slotStack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(slotStack, 1, 37, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, slotStack);
        }

        return result;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack itemStack = this.inputSlot.getStack();
        this.inputStack = itemStack.copy();
        this.updateInput(itemStack);
    }

    private void updateInput(ItemStack stack) {
        this.availableEnchantments.clear();

        if (!stack.isEmpty()) {
            DynamicRegistryManager registryManager = this.world.getRegistryManager();
            Registry<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);

            for (Map.Entry<RegistryKey<Enchantment>, Enchantment> entry : enchantmentRegistry.getEntrySet()) {
                EnchantmentEntry enchantmentEntry = new EnchantmentEntry(entry);

                if (isAcceptable(registryManager, stack, enchantmentEntry)) {
                    this.availableEnchantments.put(
                            enchantmentEntry.id(),
                            new EnchantmentProgression(
                                    0,
                                    0,
                                    EnchantmentProgressionState.UNSET
                            )
                    );
                }
            }

            EnchantmentProgressionComponent enchantmentProgressionComponent = stack.getOrDefault(
                    AHComponents.ENCHANTMENT_PROGRESSION,
                    EnchantmentProgressionComponent.DEFAULT
            );

            this.availableEnchantments.putAll(enchantmentProgressionComponent.enchantments());
        }
    }

    private boolean isAcceptable(
            DynamicRegistryManager registryManager,
            ItemStack stack,
            EnchantmentEntry enchantmentEntry
    ) {
        Registry<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);

        RegistryKey<Enchantment> enchantmentId = enchantmentEntry.id();
        Enchantment enchantment = enchantmentEntry.value();

        Optional<RegistryEntryList.Named<Enchantment>> optionalInArcaneConsole = enchantmentRegistry.getOptional(
                AHTags.Enchantments.IN_ARCANE_CONSOLE
        );

        if (optionalInArcaneConsole.isPresent()) {
            if (
                    optionalInArcaneConsole
                            .get()
                            .stream()
                            .noneMatch(entry -> entry.matchesKey(enchantmentId))
            ) {
                return false;
            }
        }

        if (stack.getItem() instanceof GauntletItem) {
            Optional<RegistryEntryList.Named<Enchantment>> optionalGauntletBlacklist = enchantmentRegistry.getOptional(
                    AHTags.Enchantments.GAUNTLET_BLACKLIST
            );

            if (optionalGauntletBlacklist.isPresent()) {
                if (
                        optionalGauntletBlacklist
                                .get()
                                .stream()
                                .anyMatch(entry -> entry.matchesKey(enchantmentId))
                ) {
                    return false;
                }
            }

            return GauntletItem.isAcceptableEnchantment(enchantment);
        }

        return enchantment.isAcceptableItem(stack);
    }
}
