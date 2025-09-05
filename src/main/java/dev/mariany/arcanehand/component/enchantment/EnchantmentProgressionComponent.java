package dev.mariany.arcanehand.component.enchantment;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public record EnchantmentProgressionComponent(
        ImmutableMap<RegistryKey<Enchantment>, EnchantmentProgression> enchantments,
        int selectedEnchantment
) implements TooltipAppender, TooltipData {
    public static final EnchantmentProgressionComponent DEFAULT = new EnchantmentProgressionComponent(new HashMap<>());

    private static final Codec<RegistryKey<Enchantment>> ENCHANTMENT_CODEC =
            RegistryKey.createCodec(RegistryKeys.ENCHANTMENT);

    private static final PacketCodec<ByteBuf, RegistryKey<Enchantment>> ENCHANTMENT_PACKET_CODEC =
            RegistryKey.createPacketCodec(RegistryKeys.ENCHANTMENT);

    public static final Codec<EnchantmentProgressionComponent> CODEC =
            Codec.unboundedMap(ENCHANTMENT_CODEC, EnchantmentProgression.CODEC)
                 .xmap(
                         map -> new EnchantmentProgressionComponent(new HashMap<>(map)),
                         progress -> progress.enchantments
                 );

    public static final PacketCodec<RegistryByteBuf, EnchantmentProgressionComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, ENCHANTMENT_PACKET_CODEC, EnchantmentProgression.PACKET_CODEC),
            component -> component.enchantments,
            EnchantmentProgressionComponent::new
    );

    public EnchantmentProgressionComponent(Map<RegistryKey<Enchantment>, EnchantmentProgression> enchantments) {
        this(ImmutableMap.copyOf(enchantments), 0);
    }

    public static List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> sortByTooltipOrder(
            RegistryWrapper.Impl<Enchantment> enchantmentRegistry,
            List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> entries,
            boolean groupByProgression
    ) {
        Optional<RegistryEntryList.Named<Enchantment>> optionalOrderList = enchantmentRegistry.getOptional(
                EnchantmentTags.TOOLTIP_ORDER
        );

        if (optionalOrderList.isPresent()) {
            RegistryEntryList<Enchantment> orderList = optionalOrderList.get();

            Map<RegistryKey<Enchantment>, Integer> orderMap = new HashMap<>();

            int index = 0;

            for (RegistryEntry<Enchantment> entry : orderList) {
                Optional<RegistryKey<Enchantment>> optionalEnchantmentKey = entry.getKey();

                if (optionalEnchantmentKey.isPresent()) {
                    orderMap.put(optionalEnchantmentKey.get(), ++index);
                }
            }

            Comparator<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> comparator = getEntryComparator(
                    orderMap,
                    groupByProgression
            );

            return entries.stream().sorted(comparator).toList();
        }

        return entries;
    }

    private static @NotNull Comparator<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> getEntryComparator(
            Map<RegistryKey<Enchantment>, Integer> orderMap,
            boolean groupByProgression
    ) {
        Comparator<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> comparator = Comparator.comparingInt(
                e -> orderMap.getOrDefault(e.getKey(), Integer.MAX_VALUE)
        );

        if (groupByProgression) {
            comparator = Comparator
                    .comparingInt(
                            (Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry) ->
                                    entry.getValue().level() > 0 ? 0 : 1
                    )
                    .thenComparingInt(
                            entry -> orderMap.getOrDefault(
                                    entry.getKey(),
                                    Integer.MAX_VALUE
                            )
                    );
        }

        return comparator;
    }

    public static List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> getSortedEntries(
            RegistryWrapper.WrapperLookup registries,
            Map<RegistryKey<Enchantment>, EnchantmentProgression> enchantments
    ) {
        return getSortedEntries(registries, enchantments, false);
    }

    public static List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> getSortedEntries(
            RegistryWrapper.WrapperLookup registries,
            Map<RegistryKey<Enchantment>, EnchantmentProgression> enchantments,
            boolean groupByCompletion
    ) {
        return registries.getOptional(RegistryKeys.ENCHANTMENT)
                         .map(
                                 enchantmentRegistry -> sortByTooltipOrder(
                                         enchantmentRegistry,
                                         enchantments.entrySet().stream().toList(),
                                         groupByCompletion
                                 )
                         )
                         .orElse(List.of());
    }

    public boolean isEmpty() {
        return this.enchantments.isEmpty();
    }

    public ItemEnchantmentsComponent toEnchantments(DynamicRegistryManager registryManager) {
        Registry<Enchantment> enchantmentRegistry = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT);

        ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(
                ItemEnchantmentsComponent.DEFAULT
        );

        for (Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry : enchantments.entrySet()) {
            EnchantmentProgression progression = entry.getValue();

            if (progression.isEnabled()) {
                builder.set(enchantmentRegistry.getOrThrow(entry.getKey()), progression.level());
            }
        }

        return builder.build();
    }

    public EnchantmentProgressionComponent excludingUnset() {
        Map<RegistryKey<Enchantment>, EnchantmentProgression> enchantments =
                new HashMap<>(this.enchantments);

        enchantments.entrySet().removeIf(entry -> entry.getValue().isUnset());

        return new EnchantmentProgressionComponent(enchantments);
    }

    @Override
    public void appendTooltip(
            Item.TooltipContext context,
            Consumer<Text> textConsumer,
            TooltipType type,
            ComponentsAccess components
    ) {
        RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();

        if (wrapperLookup != null) {
            wrapperLookup
                    .getOptional(RegistryKeys.ENCHANTMENT)
                    .ifPresent(enchantmentRegistry -> {
                        List<Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression>> entries = sortByTooltipOrder(
                                enchantmentRegistry,
                                this.enchantments.entrySet().stream().toList(),
                                true
                        ).stream()
                         .filter(entry -> entry.getValue().isEnabled())
                         .toList();

                        int enchantCount = entries.size();

                        for (int i = 0; i < enchantCount; i++) {
                            Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry = entries.get(i);
                            RegistryKey<Enchantment> enchantmentKey = entry.getKey();
                            EnchantmentProgression progress = entry.getValue();

                            boolean isSelected = this.selectedEnchantment == i && enchantCount > 1;
                            boolean isAppliedEnchantment = progress.level() > 0;

                            Optional<RegistryEntry.Reference<Enchantment>> optionalEnchantment =
                                    enchantmentRegistry.getOptional(enchantmentKey);

                            if (optionalEnchantment.isPresent()) {
                                RegistryEntry.Reference<Enchantment> enchantment = optionalEnchantment.get();

                                int level = progress.level();
                                MutableText name;

                                if (level > 0) {
                                    name = Enchantment.getName(enchantment, level).copy();
                                } else {
                                    name = enchantment.value().description().copy();
                                }

                                Formatting color = isAppliedEnchantment ? Formatting.GRAY : Formatting.DARK_GRAY;

                                Texts.setStyleIfAbsent(name, Style.EMPTY.withColor(color).withBold(isSelected));

                                textConsumer.accept(name);
                            }
                        }
                    });
        }
    }
}
