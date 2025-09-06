package dev.mariany.arcanehand.datagen;

import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.advancement.criterion.AHCriteria;
import dev.mariany.arcanehand.item.AHItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AHAdvancementsProvider extends FabricAdvancementProvider {
    public AHAdvancementsProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        ItemStack levelUpIcon = AHItems.GAUNTLET.getDefaultStack();

        levelUpIcon.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        levelUpIcon.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(Colors.GRAY));

        AdvancementEntry gauntletAdvancement = Advancement.Builder
                .create()
                .parent(Identifier.ofVanilla("story/iron_tools"))
                .display(
                        AHItems.GAUNTLET,
                        Text.translatable(
                                "advancements.arcanehand.story.gauntlet.title"),
                        Text.translatable(
                                "advancements.arcanehand.story.gauntlet.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion(
                        "gauntlet",
                        InventoryChangedCriterion.Conditions.items(
                                AHItems.GAUNTLET)
                )
                .build(consumer, advancementId("story/gauntlet"));

        Advancement.Builder
                .create()
                .parent(gauntletAdvancement)
                .display(
                        levelUpIcon,
                        Text.translatable("advancements.arcanehand.story.level_up.title"),
                        Text.translatable("advancements.arcanehand.story.level_up.description"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        true,
                        false
                )
                .criterion(
                        "leveled_up",
                        AHCriteria.LEVELED_UP.create(new TickCriterion.Conditions(Optional.empty()))
                )
                .build(consumer, advancementId("story/level_up"));
    }

    private String advancementId(String id) {
        return ArcaneHand.id(id).toString();
    }
}
