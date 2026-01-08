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
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderCommandQueue;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
            OrderedRenderCommandQueue queue,
            int light,
            S state,
            float limbAngle,
            float limbDistance
    ) {
        if (!ArcaneHand.getConfig().renderThirdPerson) {
            return;
        }

        if (state instanceof EntityWithGauntletRenderState entityWithGauntletRenderState) {
            GauntletRenderState gauntletRenderState = entityWithGauntletRenderState.arcanehand$getGauntletRenderState();

            if (gauntletRenderState.inMainHand || gauntletRenderState.inOffHand) {
                matrices.push();

                GauntletEntityModel<S> gauntletModel = this.getUsedModel(this.getContextModel());

                gauntletModel.setVisible(false);

                this.renderArms(gauntletModel, state, gauntletRenderState, queue, matrices, light);

                matrices.pop();
            }
        }
    }

    public void renderFirstPerson(
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            ItemStack stack,
            int light,
            Arm arm
    ) {
        if (!ArcaneHand.getConfig().renderFirstPerson) {
            return;
        }

        renderArm(
                getUsedModelFirstPerson(this.getContextModel()),
                null,
                matrices,
                queue,
                getUsedTexture(this.getContextModel()),
                getUsedOverlay(this.getContextModel()),
                light,
                GauntletItem.getColor(stack),
                stack.hasGlint(),
                arm == Arm.RIGHT
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
            GauntletEntityModel<S> gauntletModel,
            S state,
            GauntletRenderState gauntletRenderState,
            OrderedRenderCommandQueue queue,
            MatrixStack matrices,
            int light
    ) {
        Identifier texture = this.getUsedTexture(this.getContextModel());
        Identifier overlay = this.getUsedOverlay(this.getContextModel());

        if (gauntletRenderState.inMainHand) {
            renderArm(
                    gauntletModel,
                    state,
                    matrices,
                    queue,
                    texture,
                    overlay,
                    light,
                    gauntletRenderState.mainHandColor,
                    gauntletRenderState.mainHandGlinted,
                    gauntletRenderState.mainHandAlignedRight
            );
        }

        if (gauntletRenderState.inOffHand) {
            renderArm(
                    gauntletModel,
                    state,
                    matrices,
                    queue,
                    texture,
                    overlay,
                    light,
                    gauntletRenderState.offHandColor,
                    gauntletRenderState.offHandGlinted,
                    !gauntletRenderState.mainHandAlignedRight
            );
        }
    }

    private void renderArm(
            GauntletEntityModel<S> gauntletModel,
            @Nullable S state,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            Identifier gauntletTexture,
            Identifier overlayTexture,
            int light,
            int color,
            boolean glint,
            boolean rightArm
    ) {
        renderModel(
                gauntletModel,
                state,
                matrices,
                queue,
                gauntletTexture,
                light,
                color,
                glint,
                rightArm
        );

        renderModel(
                gauntletModel,
                state,
                matrices,
                queue,
                overlayTexture,
                light,
                -1, // tint color not applied to overlay
                glint,
                rightArm
        );
    }

    private void renderModel(
            GauntletEntityModel<S> gauntletModel,
            @Nullable S state,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            Identifier texture,
            int light,
            int color,
            boolean glint,
            boolean rightArm
    ) {
        RenderLayer renderLayer = gauntletModel.getLayer(texture);

        this.submitModel(gauntletModel, renderLayer, matrices, queue, light, color, glint, rightArm);

        if (state != null) {
            this.submitTransforms(gauntletModel, state, renderLayer, matrices, queue, light, color, rightArm);
        }
    }

    private void submitModel(
            GauntletEntityModel<S> gauntletModel,
            RenderLayer renderLayer,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light,
            int color,
            boolean glint,
            boolean rightArm
    ) {
        this.getContextModel().copyTransforms(gauntletModel);

        List<RenderLayer> layers = ItemRenderer.getGlintRenderLayers(
                renderLayer,
                false,
                glint
        );

        for (RenderLayer layer : layers) {
            ModelPart armPart = rightArm ? gauntletModel.rightArm : gauntletModel.leftArm;

            queue.submitModelPart(
                    armPart,
                    matrices,
                    layer,
                    light,
                    OverlayTexture.DEFAULT_UV,
                    null,
                    color,
                    null
            );
        }
    }

    private void submitTransforms(
            GauntletEntityModel<S> gauntletModel,
            S state,
            RenderLayer renderLayer,
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            int light,
            int color,
            boolean rightArm
    ) {
        M contextModel = this.getContextModel();
        RenderCommandQueue renderCommandQueue = queue.getBatchingQueue(0);

        if (rightArm) {
            gauntletModel.rightArm.visible = true;
        } else {
            gauntletModel.leftArm.visible = true;
        }

        ArmorRenderer.submitTransformCopyingModel(
                contextModel,
                state,
                gauntletModel,
                state,
                true,
                renderCommandQueue,
                matrices,
                renderLayer,
                light,
                OverlayTexture.DEFAULT_UV,
                color,
                null,
                0,
                null
        );
    }
}
