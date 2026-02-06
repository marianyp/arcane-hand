package dev.mariany.arcanehand.item;

import com.google.common.collect.ImmutableMap;
import dev.mariany.arcanehand.advancement.criterion.AHCriteria;
import dev.mariany.arcanehand.component.AHComponents;
import dev.mariany.arcanehand.component.type.EnchantmentProgressionComponent;
import dev.mariany.arcanehand.enchantment.EnchantmentProgression;
import dev.mariany.arcanehand.enchantment.EnchantmentProgressionState;
import dev.mariany.arcanehand.tag.AHTags;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class GauntletItem extends Item {
    public static final int DEFAULT_GAUNTLET_COLOR = -6265536;

    private static final List<TagKey<Item>> GAUNTLET_ENCHANTABLE = List.of(
            AHTags.Items.GAUNTLET_ENCHANTABLE,
            ItemTags.ARMOR_ENCHANTABLE,
            ItemTags.DURABILITY_ENCHANTABLE,
            ItemTags.MINING_ENCHANTABLE,
            ItemTags.MINING_LOOT_ENCHANTABLE,
            ItemTags.SWORD_ENCHANTABLE,
            ItemTags.WEAPON_ENCHANTABLE
    );

    public GauntletItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user) {
        return hasNeededDurability(world, pos, stack) && super.canMine(stack, state, world, pos, user);
    }

    public static boolean hasNeededDurability(World world, BlockPos pos, ItemStack stack) {
        return world.getBlockState(pos).getHardness(world, pos) <= 0 || !isConsideredBroken(stack);
    }

    private static boolean isConsideredBroken(ItemStack stack) {
        return stack.shouldBreak() || stack.willBreakNextUse();
    }

    @Override
    public void appendTooltip(
            ItemStack stack,
            TooltipContext context,
            TooltipDisplayComponent displayComponent,
            Consumer<Text> textConsumer,
            TooltipType type
    ) {
        if (isConsideredBroken(stack)) {
            MutableText text = Text.translatable("item.arcanehand.gauntlet.broken");
            Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(Formatting.GRAY).withItalic(true));
            textConsumer.accept(text);
        }
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        TooltipDisplayComponent tooltipDisplayComponent = stack.getOrDefault(
                DataComponentTypes.TOOLTIP_DISPLAY,
                TooltipDisplayComponent.DEFAULT
        );

        return tooltipDisplayComponent.shouldDisplay(AHComponents.ENCHANTMENT_PROGRESSION)
                ? Optional.ofNullable(stack.get(AHComponents.ENCHANTMENT_PROGRESSION))
                : Optional.empty();
    }

    public static boolean hasDynamicEquipSound(ItemStack stack) {
        return isGauntlet(stack);
    }

    public static boolean hasNoRepairCost(ItemStack stack) {
        return isGauntlet(stack);
    }

    public static boolean isGauntlet(ItemStack stack) {
        return stack.getItem() instanceof GauntletItem;
    }

    public static int getColor(ItemStack stack) {
        if (isGauntlet(stack)) {
            int color = DyedColorComponent.getColor(stack, 0);
            return color != 0 ? color : DEFAULT_GAUNTLET_COLOR;
        }

        return DEFAULT_GAUNTLET_COLOR;
    }

    public static boolean isAcceptableEnchantment(Enchantment enchantment) {
        return enchantment
                .definition()
                .supportedItems()
                .getTagKey()
                .map(GAUNTLET_ENCHANTABLE::contains)
                .orElse(false);
    }

    public static ImmutableMap<RegistryKey<Enchantment>, EnchantmentProgression> getEnchantments(ItemStack stack) {
        return stack
                .getOrDefault(AHComponents.ENCHANTMENT_PROGRESSION, EnchantmentProgressionComponent.DEFAULT)
                .enchantments();
    }

    public static int getEnchantmentCount(ItemStack stack) {
        return Math.toIntExact(
                getEnchantments(stack)
                        .values()
                        .stream()
                        .filter(EnchantmentProgression::isEnabled)
                        .count()
        );
    }

    public static int getSelectedEnchantmentIndex(ItemStack stack) {
        return stack.getOrDefault(AHComponents.ENCHANTMENT_PROGRESSION, EnchantmentProgressionComponent.DEFAULT)
                    .selectedEnchantment();
    }

    public static void setSelectedEnchantmentIndex(ItemStack stack, int selectedEnchantmentIndex) {
        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.get(
                AHComponents.ENCHANTMENT_PROGRESSION
        );

        if (enchantmentProgressionComponent != null) {
            stack.set(
                    AHComponents.ENCHANTMENT_PROGRESSION,
                    new EnchantmentProgressionComponent(
                            enchantmentProgressionComponent.enchantments(),
                            selectedEnchantmentIndex
                    )
            );
        }
    }

    public static void applyProgress(
            DynamicRegistryManager dynamicRegistryManager,
            RegistryKey<Enchantment> enchantmentKey,
            EnchantmentProgression progress,
            ItemStack stack
    ) {
        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.getOrDefault(
                AHComponents.ENCHANTMENT_PROGRESSION,
                EnchantmentProgressionComponent.DEFAULT
        );

        Map<RegistryKey<Enchantment>, EnchantmentProgression> progression =
                new HashMap<>(enchantmentProgressionComponent.enchantments());

        progression.put(enchantmentKey, progress);

        applyProgress(dynamicRegistryManager, progression, stack);
    }

    public static void applyProgress(
            DynamicRegistryManager dynamicRegistryManager,
            Map<RegistryKey<Enchantment>, EnchantmentProgression> progression,
            ItemStack stack
    ) {
        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.getOrDefault(
                AHComponents.ENCHANTMENT_PROGRESSION,
                EnchantmentProgressionComponent.DEFAULT
        );

        EnchantmentProgressionComponent progressionComponent = new EnchantmentProgressionComponent(
                progression,
                enchantmentProgressionComponent.selectedEnchantment()
        ).excludingUnset();

        stack.set(AHComponents.ENCHANTMENT_PROGRESSION, progressionComponent);
        stack.set(DataComponentTypes.ENCHANTMENTS, progressionComponent.toEnchantments(dynamicRegistryManager));
    }

    public static int handleExperienceCollection(PlayerEntity player, int amount) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            int mainHandRemainder = progress(serverPlayer, serverPlayer.getMainHandStack(), amount);
            return progress(serverPlayer, player.getOffHandStack(), mainHandRemainder);
        }

        return amount;
    }

    public static int progress(ServerPlayerEntity player, ItemStack stack, int experience) {
        ServerWorld world = player.getEntityWorld();
        DynamicRegistryManager registryManager = world.getRegistryManager();
        Registry<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);

        EnchantmentProgressionComponent enchantmentProgressionComponent = stack.getOrDefault(
                AHComponents.ENCHANTMENT_PROGRESSION,
                EnchantmentProgressionComponent.DEFAULT
        );

        if (enchantmentProgressionComponent.isEmpty()) {
            return experience;
        }

        Map<RegistryKey<Enchantment>, EnchantmentProgression> progression = new HashMap<>();

        List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> entries = new ArrayList<>(
                enchantmentProgressionComponent.enchantments().entrySet()
        );

        Collections.shuffle(entries, world.random::nextLong);

        int remaining = experience;
        int skipped = 0;

        for (Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry : entries) {
            RegistryKey<Enchantment> enchantmentKey = entry.getKey();
            EnchantmentProgression progress = entry.getValue();
            boolean enabled = progress.isEnabled();
            int level = progress.level();
            int nextLevel = level + 1;

            Optional<Enchantment> optionalEnchantment = enchantmentRegistry.getOptionalValue(enchantmentKey);

            if (optionalEnchantment.isPresent()) {
                Enchantment enchantment = optionalEnchantment.get();
                Enchantment.Definition enchantmentDefinition = enchantment.definition();

                if (!enabled || level >= enchantmentDefinition.maxLevel()) {
                    progression.put(enchantmentKey, progress);
                    ++skipped;
                } else {
                    int earnedExperience = progress.earnedExperience();
                    int cost = progress.getUpgradeCost(enchantmentDefinition);

                    int consumed;

                    if (remaining >= cost - earnedExperience) {
                        consumed = cost - earnedExperience;

                        progression.put(
                                enchantmentKey,
                                new EnchantmentProgression(
                                        nextLevel,
                                        0,
                                        EnchantmentProgressionState.ENABLED
                                )
                        );

                        AHCriteria.GAUNTLET_LEVELED_UP.trigger(player);
                    } else {
                        consumed = remaining;
                        remaining = 0;

                        progression.put(
                                enchantmentKey,
                                new EnchantmentProgression(
                                        level,
                                        earnedExperience + consumed,
                                        EnchantmentProgressionState.ENABLED
                                )
                        );
                    }

                    remaining = Math.max(0, remaining - consumed);
                }
            }
        }

        applyProgress(registryManager, progression, stack);

        if (remaining > 0 && skipped < progression.size()) {
            return progress(player, stack, remaining);
        }

        return remaining;
    }
}
