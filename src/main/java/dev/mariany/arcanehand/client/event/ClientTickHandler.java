package dev.mariany.arcanehand.client.event;

import dev.mariany.arcanehand.item.AHItems;
import dev.mariany.arcanehand.mixin.accessor.EntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClientTickHandler {
    private static final ItemStack DEFAULT_STACK = ItemStack.EMPTY;

    private static ItemStack previousMainHandItem = DEFAULT_STACK;
    private static ItemStack previousOffHandItem = DEFAULT_STACK;

    public static void bootstrap() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::onClientTick);
    }

    private static void onClientTick(MinecraftClient minecraftClient) {
        ClientPlayerEntity player = minecraftClient.player;

        if (player != null) {
            ItemStack mainHandItem = player.getMainHandStack();
            ItemStack offHandItem = player.getOffHandStack();

            if (player.isAlive() && !((EntityAccessor) player).arcanehand$firstUpdate()) {
                checkGauntletChange(previousMainHandItem, mainHandItem, player);
                checkGauntletChange(previousOffHandItem, offHandItem, player);
            }

            previousMainHandItem = mainHandItem;
            previousOffHandItem = offHandItem;
        }
    }

    private static void checkGauntletChange(
            @Nullable ItemStack previous,
            ItemStack current,
            ClientPlayerEntity player
    ) {
        if (previous != null) {
            if (current.isOf(AHItems.GAUNTLET) && !ItemStack.areItemsEqual(current, previous)) {
                playEquipmentSound(player.clientWorld, player);
            }
        }
    }

    private static void playEquipmentSound(ClientWorld world, ClientPlayerEntity player) {
        world.playSound(
                player,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                SoundCategory.PLAYERS,
                1F,
                1F,
                world.random.nextLong()
        );
    }
}

