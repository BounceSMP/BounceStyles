package dev.bsmp.bouncestyles.client.renderer;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleRegistry;
import dev.bsmp.bouncestyles.data.MissingStyle;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.FastBoneFilterGeoLayer;
import software.bernie.geckolib.util.RenderUtils;

public class StyleLayerRenderer extends FeatureRenderer<PlayerEntity, PlayerEntityModel<PlayerEntity>> implements GeoRenderer<Style> {
    private PlayerEntity currentPlayer;
    private StyleModel model = new StyleModel();

    public static String headBone = "armorHead";
    public static String bodyBone = "armorBody";
    public static String rightArmBone = "armorRightArm";
    public static String leftArmBone = "armorLeftArm";
    public static String rightLegBone = "armorRightLeg";
    public static String leftLegBone = "armorLeftLeg";
    public static String rightBootBone = "armorRightBoot";
    public static String leftBootBone = "armorLeftBoot";

    public StyleLayerRenderer(FeatureRendererContext<PlayerEntity, PlayerEntityModel<PlayerEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack poseStack, VertexConsumerProvider vertexConsumers, int light, PlayerEntity player, float limbAngle, float limbDistance, float partialTick, float animationProgress, float headYaw, float headPitch) {
        this.currentPlayer = player;
        StyleData styleData = StyleData.getOrCreateStyleData(player);

        poseStack.translate(0.0D, 1.497F, 0.0D);
        poseStack.scale(-1.005F, -1.0F, 1.005F);
        poseStack.push();

        renderStyle(poseStack, styleData.getHeadStyle(), StyleRegistry.Category.Head, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getBodyStyle(), StyleRegistry.Category.Body, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getLegStyle(), StyleRegistry.Category.Legs, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getFeetStyle(), StyleRegistry.Category.Feet, vertexConsumers, headYaw, partialTick, light, false);

        poseStack.pop();
        poseStack.scale(-1.005F, -1.0F, 1.005F);
        poseStack.translate(0.0D, -1.497F, 0.0D);
    }

    public void renderStyle(MatrixStack poseStack, Style style, StyleRegistry.Category category, VertexConsumerProvider vertexConsumers, float headYaw, float partialTick, int light, boolean isGui) {
        if (style == null) return;
        
        RenderLayer renderLayer = getRenderType(style, getTextureLocation(style), vertexConsumers, partialTick);
        fit(poseStack, model.getBakedModel(style.modelID), category, isGui);
        defaultRender(poseStack, style, vertexConsumers, renderLayer, null, headYaw, partialTick, light);
    }

    @Override
    public void actuallyRender(MatrixStack poseStack, Style style, BakedGeoModel model, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!isReRender) {
            boolean isMoving = false;
            if (currentPlayer != null) {
                float motionThreshold = getMotionAnimThreshold(style);
                Vec3d velocity = currentPlayer.getVelocity();
                float averageVelocity = (float) (Math.abs(velocity.x) + Math.abs(velocity.z) / 2f);
                isMoving = averageVelocity >= motionThreshold;
            }

            AnimationState<Style> animationState = new AnimationState<>(style, 0, 0, partialTick, isMoving);
            long instanceId = getInstanceId(style);

            animationState.setData(Style.PLAYER, this.currentPlayer);
            this.model.addAdditionalStateData(style, instanceId, animationState::setData);
            this.model.handleAnimations(style, instanceId, animationState);
        }
        GeoRenderer.super.actuallyRender(poseStack, style, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void fit(MatrixStack poseStack, BakedGeoModel model, StyleRegistry.Category category, boolean gui) {
        setBoneVisibility(headBone, model, false);
        setBoneVisibility(bodyBone, model, false);
        setBoneVisibility(rightArmBone, model, false);
        setBoneVisibility(leftArmBone, model, false);
        setBoneVisibility(rightLegBone, model, false);
        setBoneVisibility(leftLegBone, model, false);
        setBoneVisibility(rightBootBone, model, false);
        setBoneVisibility(rightBootBone, model, false);
        setBoneVisibility(leftBootBone, model, false);
        PlayerEntityModel<PlayerEntity> playerModel = getContextModel();

        switch (category) {
            case Head -> {
                GeoBone bone = model.getBone(headBone).orElse(null);
                if (bone != null) {
                    if (!gui)
                        RenderUtils.matchModelPartRot(getContextModel().head, bone);
                    else
                        RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
                    setBoneVisibility(headBone, model, true);
                    bone.setModelPosition(new Vector3d(playerModel.head.pivotX, -playerModel.head.pivotY, playerModel.head.pivotZ));
                }
            }
            case Body -> {
                GeoBone bodyGeoBone = model.getBone(bodyBone).orElse(null);
                GeoBone rightArmGeoBone = model.getBone(rightArmBone).orElse(null);
                GeoBone leftArmGeoBone = model.getBone(leftArmBone).orElse(null);
                if (bodyGeoBone != null && rightArmGeoBone != null && leftArmGeoBone != null) {
                    if (!gui) {
                        RenderUtils.matchModelPartRot(getContextModel().body, bodyGeoBone);
                        RenderUtils.matchModelPartRot(getContextModel().rightArm, rightArmGeoBone);
                        RenderUtils.matchModelPartRot(getContextModel().leftArm, leftArmGeoBone);
                    } else {
                        RenderUtils.translateAwayFromPivotPoint(poseStack, bodyGeoBone);
                    }
                    setBoneVisibility(bodyBone, model, true);
                    setBoneVisibility(rightArmBone, model, true);
                    setBoneVisibility(leftArmBone, model, true);
                    bodyGeoBone.setModelPosition(new Vector3d(playerModel.body.pivotX, -playerModel.body.pivotY, playerModel.body.pivotZ));
                    rightArmGeoBone.setModelPosition(new Vector3d(playerModel.rightArm.pivotX + 5, 2 - playerModel.rightArm.pivotY, playerModel.rightArm.pivotZ));
                    leftArmGeoBone.setModelPosition(new Vector3d(playerModel.leftArm.pivotX - 5, 2 - playerModel.leftArm.pivotY, playerModel.leftArm.pivotZ));
                }
            }
            case Legs -> {
                GeoBone rightLegGeoBone = model.getBone(rightLegBone).orElse(null);
                GeoBone leftLegGeoBone = model.getBone(leftLegBone).orElse(null);
                if (rightLegGeoBone != null && leftLegGeoBone != null) {
                    if (!gui) {
                        RenderUtils.matchModelPartRot(getContextModel().rightLeg, rightLegGeoBone);
                        RenderUtils.matchModelPartRot(getContextModel().leftLeg, leftLegGeoBone);
                    } else {
                        RenderUtils.translateAwayFromPivotPoint(poseStack, rightLegGeoBone);
                        RenderUtils.translateAwayFromPivotPoint(poseStack, leftLegGeoBone);
                    }
                    setBoneVisibility(rightLegBone, model, true);
                    setBoneVisibility(leftLegBone, model, true);
                    rightLegGeoBone.setModelPosition(new Vector3d(playerModel.rightLeg.pivotX + 2, 12 - playerModel.rightLeg.pivotY, playerModel.rightLeg.pivotZ));
                    leftLegGeoBone.setModelPosition(new Vector3d(playerModel.leftLeg.pivotX - 2, 12 - playerModel.leftLeg.pivotY, playerModel.leftLeg.pivotZ));
                }
            }
            case Feet -> {
                GeoBone rightBootGeoBone = model.getBone(rightBootBone).orElse(null);
                GeoBone leftBootGeoBone = model.getBone(leftBootBone).orElse(null);
                if (rightBootGeoBone != null && leftBootGeoBone != null) {
                    if (!gui) {
                        RenderUtils.matchModelPartRot(getContextModel().rightLeg, rightBootGeoBone);
                        RenderUtils.matchModelPartRot(getContextModel().leftLeg, leftBootGeoBone);
                    } else {
                        RenderUtils.translateAwayFromPivotPoint(poseStack, rightBootGeoBone);
                        RenderUtils.translateAwayFromPivotPoint(poseStack, leftBootGeoBone);
                    }
                    setBoneVisibility(rightBootBone, model, true);
                    setBoneVisibility(leftBootBone, model, true);
                    rightBootGeoBone.setModelPosition(new Vector3d(playerModel.rightLeg.pivotX + 2, 12 - playerModel.rightLeg.pivotY, playerModel.rightLeg.pivotZ));
                    leftBootGeoBone.setModelPosition(new Vector3d(playerModel.leftLeg.pivotX - 2, 12 - playerModel.leftLeg.pivotY, playerModel.leftLeg.pivotZ));
                }
            }
        }
    }

    private void setBoneVisibility(String bone, BakedGeoModel model, boolean isVisible) {
        try {
            model.getBone(bone).ifPresent(geoBone -> geoBone.setHidden(!isVisible));
        }
        catch (RuntimeException e) {
            BounceStyles.LOGGER.info("Could not find bone ["+bone+"]");
        }
    }

    @Override
    public GeoModel<Style> getGeoModel() {
        return model;
    }

    @Override
    public Style getAnimatable() {
        return null;
    }

    @Override
    public void fireCompileRenderLayersEvent() {}

    @Override
    public boolean firePreRenderEvent(MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource, float partialTick, int packedLight) {
        return true;
    }

    @Override
    public void firePostRenderEvent(MatrixStack poseStack, BakedGeoModel model, VertexConsumerProvider bufferSource, float partialTick, int packedLight) {}

    @Override
    public void updateAnimatedTextureFrame(Style animatable) {}
}
