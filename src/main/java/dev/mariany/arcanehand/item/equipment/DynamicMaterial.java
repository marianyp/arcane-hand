package dev.mariany.arcanehand.item.equipment;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.WeaponComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public record DynamicMaterial(
        TagKey<Block> incorrectBlocksForDrops,
        TagKey<Item> repairItems,
        int defense,
        float attackDamageBonus,
        float knockbackResistance,
        float speed,
        float toughness,
        int durability,
        int enchantmentValue
) {
    public Item.Settings createSettings(
            TagKey<Block> effectiveBlocks,
            float attackDamage,
            float attackSpeed,
            float disableBlockingForSeconds
    ) {
        return new Item.Settings()
                .tool(
                        this.asToolMaterial(),
                        effectiveBlocks,
                        attackDamage,
                        attackSpeed,
                        disableBlockingForSeconds
                )
                .component(
                        DataComponentTypes.EQUIPPABLE,
                        EquippableComponent.builder(EquipmentSlot.MAINHAND)
                                           .swappable(false)
                                           .build()
                )
                .component(DataComponentTypes.WEAPON, new WeaponComponent(1))
                .attributeModifiers(this.createAttributeModifiers(EquipmentType.BODY, attackDamage, attackSpeed));
    }

    public ToolMaterial asToolMaterial() {
        return new ToolMaterial(
                this.incorrectBlocksForDrops,
                this.durability,
                this.speed,
                this.attackDamageBonus,
                this.enchantmentValue,
                this.repairItems
        );
    }

    public AttributeModifiersComponent createAttributeModifiers(
            EquipmentType equipmentType,
            float attackDamage,
            float attackSpeed
    ) {
        AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();

        applyArmorAttributeModifiers(builder, equipmentType);
        applyToolAttributeModifiers(builder, attackDamage, attackSpeed);

        return builder.build();
    }

    private void applyArmorAttributeModifiers(
            AttributeModifiersComponent.Builder builder,
            EquipmentType equipmentType
    ) {
        Identifier modifierId = Identifier.ofVanilla("armor." + equipmentType.getName());

        builder.add(
                       EntityAttributes.ARMOR,
                       new EntityAttributeModifier(
                               modifierId,
                               this.defense,
                               EntityAttributeModifier.Operation.ADD_VALUE
                       ),
                       AttributeModifierSlot.HAND
               )
               .add(
                       EntityAttributes.ARMOR_TOUGHNESS,
                       new EntityAttributeModifier(
                               modifierId,
                               this.toughness,
                               EntityAttributeModifier.Operation.ADD_VALUE
                       ),
                       AttributeModifierSlot.HAND
               );

        if (this.knockbackResistance > 0) {
            builder.add(
                    EntityAttributes.KNOCKBACK_RESISTANCE,
                    new EntityAttributeModifier(
                            modifierId,
                            this.knockbackResistance,
                            EntityAttributeModifier.Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.HAND
            );
        }
    }

    private void applyToolAttributeModifiers(
            AttributeModifiersComponent.Builder builder,
            float attackDamage,
            float attackSpeed
    ) {
        builder.add(
                       EntityAttributes.ATTACK_DAMAGE,
                       new EntityAttributeModifier(
                               Item.BASE_ATTACK_DAMAGE_MODIFIER_ID,
                               attackDamage + this.attackDamageBonus,
                               EntityAttributeModifier.Operation.ADD_VALUE
                       ),
                       AttributeModifierSlot.MAINHAND
               )
               .add(
                       EntityAttributes.ATTACK_SPEED,
                       new EntityAttributeModifier(
                               Item.BASE_ATTACK_SPEED_MODIFIER_ID,
                               attackSpeed,
                               EntityAttributeModifier.Operation.ADD_VALUE
                       ),
                       AttributeModifierSlot.MAINHAND
               );
    }
}
