package dev.mariany.arcanehand.component.enchantment;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Consumer;

public class EnchantmentProgressionComponent implements TooltipAppender {
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

    private final Map<RegistryKey<Enchantment>, EnchantmentProgression> enchantments;

    public EnchantmentProgressionComponent(Map<RegistryKey<Enchantment>, EnchantmentProgression> enchantments) {
        this.enchantments = enchantments;
    }

    @Override
    public void appendTooltip(
            Item.TooltipContext context,
            Consumer<Text> textConsumer,
            TooltipType type,
            ComponentsAccess components
    ) {
    }

    public Map<RegistryKey<Enchantment>, EnchantmentProgression> getEnchantments() {
        return ImmutableMap.copyOf(this.enchantments);
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

    public EnchantmentProgressionComponent prune() {
        for (Map.Entry<RegistryKey<Enchantment>, EnchantmentProgression> entry : this.enchantments.entrySet()) {
            if (entry.getValue().state().equals(EnchantmentProgressionState.UNSET)) {
                this.enchantments.remove(entry.getKey());
            }
        }

        return this;
    }
}
