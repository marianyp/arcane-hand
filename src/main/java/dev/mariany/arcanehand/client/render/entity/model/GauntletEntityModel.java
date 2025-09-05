package dev.mariany.arcanehand.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

@Environment(EnvType.CLIENT)
public class GauntletEntityModel<S extends BipedEntityRenderState> extends BipedEntityModel<S> {
    public GauntletEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation, boolean isSlim) {
        ModelData modelData = BipedEntityModel.getModelData(dilation, 0);
        ModelPartData modelPartData = modelData.getRoot();

        int width = 16;

        if (isSlim) {
            width = 14;
            modelPartData.addChild(
                    "right_arm",
                    ModelPartBuilder.create().cuboid(
                            -2,
                            -2,
                            -2,
                            3,
                            12,
                            4,
                            dilation
                    ),
                    ModelTransform.origin(-5, 2.5F, 0)
            );
            modelPartData.addChild(
                    "left_arm",
                    ModelPartBuilder.create().mirrored().cuboid(-1, -2, -2, 3, 12, 4, dilation),
                    ModelTransform.origin(5, 2.5F, 0)
            );
        } else {
            modelPartData.addChild(
                    "right_arm",
                    ModelPartBuilder.create().cuboid(-3, -2, -2, 4, 12, 4, dilation),
                    ModelTransform.origin(-5, 2, 0)
            );
            modelPartData.addChild(
                    "left_arm",
                    ModelPartBuilder.create().mirrored().cuboid(-1, -2, -2, 4, 12, 4, dilation),
                    ModelTransform.origin(5, 2, 0)
            );
        }

        return TexturedModelData.of(modelData, width, 16);
    }
}
