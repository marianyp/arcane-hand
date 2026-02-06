package dev.mariany.arcanehand.client.render.entity.model;

import dev.mariany.arcanehand.ArcaneHand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.model.EntityModelLayer;

@Environment(EnvType.CLIENT)
public final class AHModels {
    public static final String MAIN = "main";
    public static final EntityModelLayer GAUNTLET = registerMain("gauntlet");
    public static final EntityModelLayer GAUNTLET_SLIM = registerMain("gauntlet_slim");
    public static final EntityModelLayer GAUNTLET_FIRST_PERSON = registerMain("gauntlet_first_person");
    public static final EntityModelLayer GAUNTLET_FIRST_PERSON_SLIM = registerMain("gauntlet_first_person_slim");

    private AHModels() {
    }

    private static EntityModelLayer registerMain(String id) {
        return new EntityModelLayer(ArcaneHand.id(id), MAIN);
    }

    public static void bootstrap() {
        ArcaneHand.bootstrapLog("Models");

        EntityModelLayerRegistry.registerModelLayer(
                GAUNTLET,
                () -> GauntletEntityModel.getTexturedModelData(new Dilation(0.6F), false)
        );

        EntityModelLayerRegistry.registerModelLayer(
                GAUNTLET_SLIM,
                () -> GauntletEntityModel.getTexturedModelData(new Dilation(0.6F), true)
        );

        EntityModelLayerRegistry.registerModelLayer(
                GAUNTLET_FIRST_PERSON,
                () -> GauntletEntityModel.getTexturedModelData(new Dilation(0.25F), false)
        );

        EntityModelLayerRegistry.registerModelLayer(
                GAUNTLET_FIRST_PERSON_SLIM,
                () -> GauntletEntityModel.getTexturedModelData(new Dilation(0.25F), true)
        );
    }
}
