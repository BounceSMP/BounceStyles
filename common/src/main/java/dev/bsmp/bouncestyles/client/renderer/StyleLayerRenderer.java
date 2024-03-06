package dev.bsmp.bouncestyles.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleRegistry;
import dev.bsmp.bouncestyles.data.MissingStyle;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleData;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.FastBoneFilterGeoLayer;
import software.bernie.geckolib.util.RenderUtils;

public class StyleLayerRenderer extends RenderLayer<Player, PlayerModel<Player>> implements GeoRenderer<Style> {
    private Player currentPlayer;
    private StyleModel model = new StyleModel();

    public static String headBone = "armorHead";
    public static String bodyBone = "armorBody";
    public static String rightArmBone = "armorRightArm";
    public static String leftArmBone = "armorLeftArm";
    public static String rightLegBone = "armorRightLeg";
    public static String leftLegBone = "armorLeftLeg";
    public static String rightBootBone = "armorRightBoot";
    public static String leftBootBone = "armorLeftBoot";

    public StyleLayerRenderer(RenderLayerParent<Player, PlayerModel<Player>> context) {
        super(context);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource vertexConsumers, int light, Player player, float limbAngle, float limbDistance, float partialTick, float animationProgress, float headYaw, float headPitch) {
        this.currentPlayer = player;
        StyleData styleData = StyleData.getOrCreateStyleData(player);

        poseStack.translate(0.0D, 1.497F, 0.0D);
        poseStack.scale(-1.005F, -1.0F, 1.005F);
        poseStack.pushPose();

        renderStyle(poseStack, styleData.getHeadStyle(), StyleRegistry.Category.Head, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getBodyStyle(), StyleRegistry.Category.Body, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getLegStyle(), StyleRegistry.Category.Legs, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getFeetStyle(), StyleRegistry.Category.Feet, vertexConsumers, headYaw, partialTick, light, false);

        poseStack.popPose();
        poseStack.scale(-1.005F, -1.0F, 1.005F);
        poseStack.translate(0.0D, -1.497F, 0.0D);
    }

    public void renderStyle(PoseStack poseStack, Style style, StyleRegistry.Category category, MultiBufferSource vertexConsumers, float headYaw, float partialTick, int light, boolean isGui) {
        if (style == null) return;
        
        RenderType renderLayer = getRenderType(style, getTextureLocation(style), vertexConsumers, partialTick);
        fit(poseStack, model.getBakedModel(style.modelID), category, isGui);
        defaultRender(poseStack, style, vertexConsumers, renderLayer, null, headYaw, partialTick, light);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, Style style, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (!isReRender) {
            boolean isMoving = false;
            if (currentPlayer != null) {
                float motionThreshold = getMotionAnimThreshold(style);
                Vec3 velocity = currentPlayer.getDeltaMovement();
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

    private void fit(PoseStack poseStack, BakedGeoModel model, StyleRegistry.Category category, boolean gui) {
        setBoneVisibility(headBone, model, false);
        setBoneVisibility(bodyBone, model, false);
        setBoneVisibility(rightArmBone, model, false);
        setBoneVisibility(leftArmBone, model, false);
        setBoneVisibility(rightLegBone, model, false);
        setBoneVisibility(leftLegBone, model, false);
        setBoneVisibility(rightBootBone, model, false);
        setBoneVisibility(rightBootBone, model, false);
        setBoneVisibility(leftBootBone, model, false);
        PlayerModel<Player> playerModel = getParentModel();

        switch (category) {
            case Head -> {
                GeoBone bone = model.getBone(headBone).orElse(null);
                if (bone != null) {
                    if (!gui)
                        RenderUtils.matchModelPartRot(getParentModel().head, bone);
                    else
                        RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
                    setBoneVisibility(headBone, model, true);
                    bone.setModelPosition(new Vector3d(playerModel.head.x, -playerModel.head.y, playerModel.head.z));
                }
            }
            case Body -> {
                GeoBone bodyGeoBone = model.getBone(bodyBone).orElse(null);
                GeoBone rightArmGeoBone = model.getBone(rightArmBone).orElse(null);
                GeoBone leftArmGeoBone = model.getBone(leftArmBone).orElse(null);
                if (bodyGeoBone != null && rightArmGeoBone != null && leftArmGeoBone != null) {
                    if (!gui) {
                        RenderUtils.matchModelPartRot(getParentModel().body, bodyGeoBone);
                        RenderUtils.matchModelPartRot(getParentModel().rightArm, rightArmGeoBone);
                        RenderUtils.matchModelPartRot(getParentModel().leftArm, leftArmGeoBone);
                    } else {
                        RenderUtils.translateAwayFromPivotPoint(poseStack, bodyGeoBone);
                    }
                    setBoneVisibility(bodyBone, model, true);
                    setBoneVisibility(rightArmBone, model, true);
                    setBoneVisibility(leftArmBone, model, true);
                    bodyGeoBone.setModelPosition(new Vector3d(playerModel.body.x, -playerModel.body.y, playerModel.body.z));
                    rightArmGeoBone.setModelPosition(new Vector3d(playerModel.rightArm.x + 5, 2 - playerModel.rightArm.y, playerModel.rightArm.z));
                    leftArmGeoBone.setModelPosition(new Vector3d(playerModel.leftArm.x - 5, 2 - playerModel.leftArm.y, playerModel.leftArm.z));
                }
            }
            case Legs -> {
                GeoBone rightLegGeoBone = model.getBone(rightLegBone).orElse(null);
                GeoBone leftLegGeoBone = model.getBone(leftLegBone).orElse(null);
                if (rightLegGeoBone != null && leftLegGeoBone != null) {
                    if (!gui) {
                        RenderUtils.matchModelPartRot(getParentModel().rightLeg, rightLegGeoBone);
                        RenderUtils.matchModelPartRot(getParentModel().leftLeg, leftLegGeoBone);
                    } else {
                        RenderUtils.translateAwayFromPivotPoint(poseStack, rightLegGeoBone);
                        RenderUtils.translateAwayFromPivotPoint(poseStack, leftLegGeoBone);
                    }
                    setBoneVisibility(rightLegBone, model, true);
                    setBoneVisibility(leftLegBone, model, true);
                    rightLegGeoBone.setModelPosition(new Vector3d(playerModel.rightLeg.x + 2, 12 - playerModel.rightLeg.y, playerModel.rightLeg.z));
                    leftLegGeoBone.setModelPosition(new Vector3d(playerModel.leftLeg.x - 2, 12 - playerModel.leftLeg.y, playerModel.leftLeg.z));
                }
            }
            case Feet -> {
                GeoBone rightBootGeoBone = model.getBone(rightBootBone).orElse(null);
                GeoBone leftBootGeoBone = model.getBone(leftBootBone).orElse(null);
                if (rightBootGeoBone != null && leftBootGeoBone != null) {
                    if (!gui) {
                        RenderUtils.matchModelPartRot(getParentModel().rightLeg, rightBootGeoBone);
                        RenderUtils.matchModelPartRot(getParentModel().leftLeg, leftBootGeoBone);
                    } else {
                        RenderUtils.translateAwayFromPivotPoint(poseStack, rightBootGeoBone);
                        RenderUtils.translateAwayFromPivotPoint(poseStack, leftBootGeoBone);
                    }
                    setBoneVisibility(rightBootBone, model, true);
                    setBoneVisibility(leftBootBone, model, true);
                    rightBootGeoBone.setModelPosition(new Vector3d(playerModel.rightLeg.x + 2, 12 - playerModel.rightLeg.y, playerModel.rightLeg.z));
                    leftBootGeoBone.setModelPosition(new Vector3d(playerModel.leftLeg.x - 2, 12 - playerModel.leftLeg.y, playerModel.leftLeg.z));
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
    public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return true;
    }

    @Override
    public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {}

    @Override
    public void updateAnimatedTextureFrame(Style animatable) {}
}
