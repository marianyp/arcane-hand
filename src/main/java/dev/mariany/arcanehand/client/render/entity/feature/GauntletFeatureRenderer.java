package dev.mariany.arcanehand.client.render.entity.feature;

import dev.mariany.arcanehand.ArcaneHand;
import dev.mariany.arcanehand.client.render.entity.model.AHModels;
import dev.mariany.arcanehand.client.render.entity.model.GauntletEntityModel;
import dev.mariany.arcanehand.client.render.entity.state.EntityWithGauntletRenderState;
import dev.mariany.arcanehand.client.render.entity.state.GauntletRenderState;
import dev.mariany.arcanehand.item.GauntletItem;
import dev.mariany.arcanehand.mixin.accessor.PlayerEntityModelAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GauntletFeatureRenderer<S extends BipedEntityRenderState, M extends BipedEntityModel<S>>
        extends FeatureRenderer<S, M> {
    private static final String TEXTURE_ROOT = "textures/entity/equipment/humanoid/gauntlet/gauntlet";
    private static final Identifier TEXTURE = ArcaneHand.id(TEXTURE_ROOT + ".png");
    private static final Identifier TEXTURE_SLIM = ArcaneHand.id(TEXTURE_ROOT + "_slim.png");

    private static final String OVERLAY_ROOT = "textures/entity/equipment/humanoid/gauntlet/overlay";
    private static final Identifier OVERLAY = ArcaneHand.id(OVERLAY_ROOT + ".png");
    private static final Identifier OVERLAY_SLIM = ArcaneHand.id(OVERLAY_ROOT + "_slim.png");

    private final GauntletEntityModel<S> defaultModel;
    private final GauntletEntityModel<S> slimModel;
    private final GauntletEntityModel<S> firstPersonModel;
    private final GauntletEntityModel<S> firstPersonSlimModel;

    public GauntletFeatureRenderer(FeatureRendererContext<S, M> context, LoadedEntityModels models) {
        super(context);
        this.defaultModel = new GauntletEntityModel<>(models.getModelPart(AHModels.GAUNTLET));
        this.slimModel = new GauntletEntityModel<>(models.getModelPart(AHModels.GAUNTLET_SLIM));
        this.firstPersonModel = new GauntletEntityModel<>(models.getModelPart(AHModels.GAUNTLET_FIRST_PERSON));
        this.firstPersonSlimModel = new GauntletEntityModel<>(
                models.getModelPart(AHModels.GAUNTLET_FIRST_PERSON_SLIM)
        );
    }

    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            S state,
            float limbAngle,
            float limbDistance
    ) {
        if (state instanceof EntityWithGauntletRenderState entityWithGauntletRenderState) {
            GauntletRenderState gauntletRenderState = entityWithGauntletRenderState.arcanehand$getGauntletRenderState();

            if (!gauntletRenderState.inMainHand && !gauntletRenderState.inOffHand) {
                return;
            }

            matrices.push();

            GauntletEntityModel<S> gauntletModel = this.getUsedModel(this.getContextModel());

            gauntletModel.setVisible(false);

            this.renderArms(vertexConsumers, gauntletModel, gauntletRenderState, matrices, light);

            matrices.pop();
        }
    }

    public void renderFirstPerson(
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            ItemStack stack,
            int light,
            Arm arm
    ) {
        renderArmWithOverlay(
                getUsedModelFirstPerson(this.getContextModel()),
                matrices,
                vertices,
                light,
                arm == Arm.RIGHT,
                GauntletItem.getColor(stack),
                stack.hasGlint(),
                getUsedTexture(this.getContextModel()),
                getUsedOverlay(this.getContextModel())
        );
    }

    private boolean isSlim(EntityModel<?> model) {
        if (model instanceof PlayerEntityModel playerModel) {
            return ((PlayerEntityModelAccessor) playerModel).arcanehand$thinArms();
        }

        return false;
    }

    private Identifier getUsedTexture(EntityModel<?> model) {
        return this.isSlim(model) ? TEXTURE_SLIM : TEXTURE;
    }

    private Identifier getUsedOverlay(EntityModel<?> model) {
        return this.isSlim(model) ? OVERLAY_SLIM : OVERLAY;
    }

    private GauntletEntityModel<S> getUsedModel(EntityModel<?> model) {
        return this.isSlim(model) ? this.slimModel : this.defaultModel;
    }

    private GauntletEntityModel<S> getUsedModelFirstPerson(EntityModel<?> model) {
        return this.isSlim(model) ? this.firstPersonSlimModel : this.firstPersonModel;
    }

    private void renderArms(
            VertexConsumerProvider vertices,
            GauntletEntityModel<S> gauntletModel,
            GauntletRenderState gauntletRenderState,
            MatrixStack matrices,
            int light
    ) {
        Identifier texture = this.getUsedTexture(this.getContextModel());
        Identifier overlay = this.getUsedOverlay(this.getContextModel());

        if (gauntletRenderState.inMainHand) {
            renderArmWithOverlay(
                    gauntletModel,
                    matrices,
                    vertices,
                    light,
                    gauntletRenderState.mainHandAlignedRight,
                    gauntletRenderState.mainHandColor,
                    gauntletRenderState.mainHandGlinted,
                    texture,
                    overlay
            );
        }

        if (gauntletRenderState.inOffHand) {
            renderArmWithOverlay(
                    gauntletModel,
                    matrices,
                    vertices,
                    light,
                    !gauntletRenderState.mainHandAlignedRight,
                    gauntletRenderState.offHandColor,
                    gauntletRenderState.offHandGlinted,
                    texture,
                    overlay
            );
        }
    }

    private void renderArmWithOverlay(
            GauntletEntityModel<S> gauntletModel,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            int light,
            boolean rightArm,
            int color,
            boolean glint,
            Identifier gauntletTexture,
            Identifier overlayTexture
    ) {
        renderArm(
                gauntletModel,
                matrices,
                vertices,
                light,
                rightArm,
                color,
                glint,
                gauntletTexture
        );

        renderArm(
                gauntletModel,
                matrices,
                vertices,
                light,
                rightArm,
                -1,
                glint,
                overlayTexture
        );
    }

    private void renderArm(
            GauntletEntityModel<S> gauntletModel,
            MatrixStack matrices,
            VertexConsumerProvider vertices,
            int light,
            boolean rightArm,
            int color,
            boolean glint,
            Identifier texture
    ) {
        this.getContextModel().copyTransforms(gauntletModel);

        VertexConsumer consumer = ItemRenderer.getArmorGlintConsumer(
                vertices,
                RenderLayer.getArmorCutoutNoCull(texture),
                glint
        );

        if (rightArm) {
            gauntletModel.rightArm.visible = true;
            gauntletModel.rightArm.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, color);
        } else {
            gauntletModel.leftArm.visible = true;
            gauntletModel.leftArm.render(matrices, consumer, light, OverlayTexture.DEFAULT_UV, color);
        }
    }
}
