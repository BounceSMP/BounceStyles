package dev.bsmp.bouncestyles.core.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.data.StyleData;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import java.util.Optional;
//? if <= 1.20.1 {
/*import software.bernie.geckolib.core.animation.AnimationState;
*///?} else if >= 1.21.1 {
import software.bernie.geckolib.animation.AnimationState;
import org.jetbrains.annotations.Nullable;
//?}

public class StyleLayerRenderer extends RenderLayer<Player, PlayerModel<Player>> implements GeoRenderer<Style> {
    private Player currentPlayer;
    private static final StyleGeoModel geoModel = new StyleGeoModel();

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

        renderStyle(poseStack, styleData.getHeadStyle(), Category.Head, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getBodyStyle(), Category.Body, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getLegStyle(), Category.Legs, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getFeetStyle(), Category.Feet, vertexConsumers, headYaw, partialTick, light, false);

        poseStack.popPose();
        poseStack.scale(-1.005F, -1.0F, 1.005F);
        poseStack.translate(0.0D, -1.497F, 0.0D);
    }

    public void renderStyle(PoseStack poseStack, Optional<Pair<Style, Integer>> equippedStyle, Category category, MultiBufferSource vertexConsumers, float headYaw, float partialTick, int light, boolean isGui) {
        if (equippedStyle.isEmpty()) return;
        renderStyle(poseStack, equippedStyle.get().getFirst(), equippedStyle.get().getSecond(), category, vertexConsumers, headYaw, partialTick, light, isGui);
    }

    public void renderStyle(PoseStack poseStack, Style style, int textureId, Category category, MultiBufferSource bufferSource, float headYaw, float partialTick, int light, boolean isGui) {
        ResourceLocation texture = getStyleTexture(style, textureId);

        RenderType renderLayer = getRenderType(style, texture, bufferSource, partialTick);
        var bakedModel = geoModel.getBakedModel(style.getModelId());
        setupBoneVisibility(bakedModel, category);
        fitToBones(bakedModel, category);
        defaultRender(poseStack, style, bufferSource, renderLayer, null, headYaw, partialTick, light);
    }

    public void renderStyleForGUI(PoseStack poseStack, Style style, int textureId, Category category, MultiBufferSource bufferSource, float headYaw, float partialTick) {
        ResourceLocation texture = getStyleTexture(style, textureId);

        RenderType renderType = getRenderType(style, texture, bufferSource, partialTick);
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        var bakedModel = geoModel.getBakedModel(style.getModelId());
        setupBoneVisibility(bakedModel, category);

        poseStack.pushPose();

        switch (category) {
            case Head -> {
                moveFromPivot(poseStack, bakedModel.getBone(headBone).get());
                poseStack.translate(0, -.7f, 0);
            }
            case Body -> moveFromPivot(poseStack, bakedModel.getBone(bodyBone).get());
            case Legs -> {
                moveFromPivot(poseStack, bakedModel.getBone(leftLegBone).get());
                poseStack.translate(.1f, 0, 0);
            }
            case Feet -> {
                moveFromPivot(poseStack, bakedModel.getBone(leftBootBone).get());
                poseStack.translate(.1f, 0, 0);
            }
        }

        poseStack.translate(0, 1.1f, 0);
        defaultRender(poseStack, style, bufferSource, renderType, buffer, 0f, partialTick, 15728880);

        poseStack.popPose();
    }

    private void moveFromPivot(PoseStack poseStack, GeoBone bone) {
        //? if <= 1.20.1 {
        /*software.bernie.geckolib.util.RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
        *///?} else if >= 1.21.1 {
        software.bernie.geckolib.util.RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
        //?}
    }

    //? if <= 1.20.1 {
    /*@Override
    public void actuallyRender(PoseStack poseStack, Style style, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        setupAnimation(style, isReRender, partialTick);
        GeoRenderer.super.actuallyRender(poseStack, style, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
    *///?} else if >= 1.21.1 {
    @Override
    public void actuallyRender(PoseStack poseStack, Style style, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        setupAnimation(style, isReRender, partialTick);
        GeoRenderer.super.actuallyRender(poseStack, style, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
    //?}

    private static ResourceLocation getStyleTexture(Style style, int textureId) {
        ResourceLocation texture = style.getTextureId();
        if (textureId >= 0 && style.getTextureVariants().isPresent()) {
            var textures = style.getTextureVariants().get();
            if (textures.size() > textureId) texture = textures.get(textureId);
        }
        return texture;
    }

    private void setupAnimation(Style style, boolean isReRender, float partialTick) {
        if (!isReRender) {
            boolean isMoving = false;
            long instanceId = -1;

            if (currentPlayer != null) {
                double i = Math.abs(currentPlayer.getX() - currentPlayer.xOld) + Math.abs(currentPlayer.getZ() - currentPlayer.zOld);
                isMoving = i > 0.075 && currentPlayer.walkAnimation.isMoving();
                instanceId = currentPlayer.getId();
            }

            AnimationState<Style> animationState = new AnimationState<>(style, 0, 0, partialTick, isMoving);

            animationState.setData(Style.PLAYER, this.currentPlayer);
            geoModel.addAdditionalStateData(style, instanceId, animationState::setData);
            //? if <= 1.20.1 {
            /*model.handleAnimations(style, instanceId, animationState);
            *///?} else if >= 1.21.1 {
            geoModel.handleAnimations(style, instanceId, animationState, partialTick);
            //?}
        }
    }

    private void setupBoneVisibility(BakedGeoModel model, Category category) {
        setBoneVisibility(headBone, model, false);
        setBoneVisibility(bodyBone, model, false);
        setBoneVisibility(rightArmBone, model, false);
        setBoneVisibility(leftArmBone, model, false);
        setBoneVisibility(rightLegBone, model, false);
        setBoneVisibility(leftLegBone, model, false);
        setBoneVisibility(rightBootBone, model, false);
        setBoneVisibility(rightBootBone, model, false);
        setBoneVisibility(leftBootBone, model, false);

        switch (category) {
            case Head -> setBoneVisibility(headBone, model, true);
            case Body -> {
                setBoneVisibility(bodyBone, model, true);
                setBoneVisibility(rightArmBone, model, true);
                setBoneVisibility(leftArmBone, model, true);
            }
            case Legs -> {
                setBoneVisibility(rightLegBone, model, true);
                setBoneVisibility(leftLegBone, model, true);
            }
            case Feet -> {
                setBoneVisibility(rightBootBone, model, true);
                setBoneVisibility(leftBootBone, model, true);
            }
        }
    }

    private void fitToBones(BakedGeoModel model, Category category) {
        PlayerModel<Player> playerModel = getParentModel();

        switch (category) {
            case Head -> {
                GeoBone bone = model.getBone(headBone).orElse(null);
                if (bone != null) {
                    matchModelPartRot(getParentModel().head, bone);
                    bone.setModelPosition(new Vector3d(playerModel.head.x, -playerModel.head.y, playerModel.head.z));
                }
            }
            case Body -> {
                GeoBone bodyGeoBone = model.getBone(bodyBone).orElse(null);
                GeoBone rightArmGeoBone = model.getBone(rightArmBone).orElse(null);
                GeoBone leftArmGeoBone = model.getBone(leftArmBone).orElse(null);
                if (bodyGeoBone != null && rightArmGeoBone != null && leftArmGeoBone != null) {
                    matchModelPartRot(getParentModel().body, bodyGeoBone);
                    matchModelPartRot(getParentModel().rightArm, rightArmGeoBone);
                    matchModelPartRot(getParentModel().leftArm, leftArmGeoBone);
                    bodyGeoBone.setModelPosition(new Vector3d(playerModel.body.x, -playerModel.body.y, playerModel.body.z));
                    rightArmGeoBone.setModelPosition(new Vector3d(playerModel.rightArm.x + 5, 2 - playerModel.rightArm.y, playerModel.rightArm.z));
                    leftArmGeoBone.setModelPosition(new Vector3d(playerModel.leftArm.x - 5, 2 - playerModel.leftArm.y, playerModel.leftArm.z));
                }
            }
            case Legs -> {
                GeoBone rightLegGeoBone = model.getBone(rightLegBone).orElse(null);
                GeoBone leftLegGeoBone = model.getBone(leftLegBone).orElse(null);
                if (rightLegGeoBone != null && leftLegGeoBone != null) {
                    matchModelPartRot(getParentModel().rightLeg, rightLegGeoBone);
                    matchModelPartRot(getParentModel().leftLeg, leftLegGeoBone);
                    rightLegGeoBone.setModelPosition(new Vector3d(playerModel.rightLeg.x + 2, 12 - playerModel.rightLeg.y, playerModel.rightLeg.z));
                    leftLegGeoBone.setModelPosition(new Vector3d(playerModel.leftLeg.x - 2, 12 - playerModel.leftLeg.y, playerModel.leftLeg.z));
                }
            }
            case Feet -> {
                GeoBone rightBootGeoBone = model.getBone(rightBootBone).orElse(null);
                GeoBone leftBootGeoBone = model.getBone(leftBootBone).orElse(null);
                if (rightBootGeoBone != null && leftBootGeoBone != null) {
                    matchModelPartRot(getParentModel().rightLeg, rightBootGeoBone);
                    matchModelPartRot(getParentModel().leftLeg, leftBootGeoBone);
                    rightBootGeoBone.setModelPosition(new Vector3d(playerModel.rightLeg.x + 2, 12 - playerModel.rightLeg.y, playerModel.rightLeg.z));
                    leftBootGeoBone.setModelPosition(new Vector3d(playerModel.leftLeg.x - 2, 12 - playerModel.leftLeg.y, playerModel.leftLeg.z));
                }
            }
        }
    }

    private void matchModelPartRot(ModelPart modelPart, GeoBone geoBone) {
        //? if <= 1.20.1 {
        /*software.bernie.geckolib.util.RenderUtils.matchModelPartRot(modelPart, geoBone);
        *///?} else if >= 1.21.1 {
        software.bernie.geckolib.util.RenderUtil.matchModelPartRot(modelPart, geoBone);
        //?}
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
    public RenderType getRenderType(Style animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(texture);
    }

    @Override
    public GeoModel<Style> getGeoModel() {
        return geoModel;
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
