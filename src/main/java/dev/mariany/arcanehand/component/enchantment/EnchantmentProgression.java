package dev.mariany.arcanehand.component.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record EnchantmentProgression(int level, int earnedExperience, EnchantmentProgressionState state) {
    public static final Codec<EnchantmentProgression> CODEC =
            RecordCodecBuilder.create(
                    instance ->
                            instance.group(
                                            Codec.INT
                                                    .fieldOf("level")
                                                    .forGetter(EnchantmentProgression::level),
                                            Codec.INT
                                                    .fieldOf("earned_experience")
                                                    .forGetter(EnchantmentProgression::earnedExperience),
                                            EnchantmentProgressionState.CODEC
                                                    .fieldOf("state")
                                                    .forGetter(EnchantmentProgression::state)
                                    )
                                    .apply(instance, EnchantmentProgression::new)
            );

    public static final PacketCodec<RegistryByteBuf, EnchantmentProgression> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT,
            EnchantmentProgression::level,
            PacketCodecs.VAR_INT,
            EnchantmentProgression::earnedExperience,
            EnchantmentProgressionState.PACKET_CODEC,
            EnchantmentProgression::state,
            EnchantmentProgression::new
    );

    public boolean isEnabled() {
        return this.state.isEnabled();
    }

    public boolean isUnset() {
        return this.state.isUnset();
    }

    public EnchantmentProgression withToggledState() {
        EnchantmentProgressionState newState = this.state.toggle();

        if (!newState.isEnabled()) {
            if (this.level <= 0 && this.earnedExperience <= 0) {
                newState = EnchantmentProgressionState.UNSET;
            }
        }

        return new EnchantmentProgression(
                this.level,
                this.earnedExperience,
                newState
        );
    }
}
