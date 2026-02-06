package dev.mariany.arcanehand.client;

import dev.mariany.arcanehand.item.GauntletItem;
import dev.mariany.arcanehand.mixin.accessor.EntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class GauntletSoundManager {
    private ItemStack previousMainHandItem = ItemStack.EMPTY;
    private ItemStack previousOffHandItem = ItemStack.EMPTY;

    public void bootstrap() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient minecraftClient) {
        ClientPlayerEntity player = minecraftClient.player;

        if (player != null) {
            ItemStack mainHandItem = player.getMainHandStack();
            ItemStack offHandItem = player.getOffHandStack();

            if (player.isAlive() && !((EntityAccessor) player).arcanehand$firstUpdate()) {
                checkGauntletChange(this.previousMainHandItem, mainHandItem, player);
                checkGauntletChange(this.previousOffHandItem, offHandItem, player);
            }

            this.previousMainHandItem = mainHandItem;
            this.previousOffHandItem = offHandItem;
        }
    }

    private static void checkGauntletChange(
            @Nullable ItemStack previousStack,
            ItemStack currentStack,
            ClientPlayerEntity player
    ) {
        if (previousStack != null) {
            if (GauntletItem.isGauntlet(currentStack) && !ItemStack.areItemsEqual(currentStack, previousStack)) {
                playEquipmentSound(player.getEntityWorld(), player);
            }
        }
    }

    private static void playEquipmentSound(World world, ClientPlayerEntity player) {
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

