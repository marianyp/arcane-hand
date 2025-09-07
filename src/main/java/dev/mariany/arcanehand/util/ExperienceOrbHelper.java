package dev.mariany.arcanehand.util;

import dev.mariany.arcanehand.item.GauntletItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ExperienceOrbHelper {
    public static int handleExperienceCollection(PlayerEntity player, int amount) {
        if(player instanceof ServerPlayerEntity serverPlayer) {
            int mainHandRemainder = GauntletItem.progress(serverPlayer, serverPlayer.getMainHandStack(), amount);
            return GauntletItem.progress(serverPlayer, player.getOffHandStack(), mainHandRemainder);
        }

        return amount;
    }
}
