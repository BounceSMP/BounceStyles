//? if <= 1.21.1 {
/*package dev.bsmp.bouncestyles.core.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.bsmp.bouncestyles.api.animation.AnimState;
import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.api.data.EquippedStyle;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.data.animation.AnimationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.HashMap;
import java.util.List;

public class LegacyStyleLayerRenderer extends RenderLayer<Player, PlayerModel<Player>> implements GeoRenderer<Style> {
    private static final StyleGeoModel geoModel = new StyleGeoModel();
    private final List<GeoRenderLayer<Style>> renderLayers = List.of(
            new AutoGlowingGeoLayer<>(this)
    );

    public static String headBone = "armorHead";
    public static String bodyBone = "armorBody";
    public static String rightArmBone = "armorRightArm";
    public static String leftArmBone = "armorLeftArm";
    public static String rightLegBone = "armorRightLeg";
    public static String leftLegBone = "armorLeftLeg";
    public static String rightBootBone = "armorRightBoot";
    public static String leftBootBone = "armorLeftBoot";

    private Player currentPlayer;
    private final HashMap<Identifier, Boolean> emissiveCache = new HashMap<>();
    public LegacyStyleLayerRenderer(RenderLayerParent<Player, PlayerModel<Player>> context) {
        super(context);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource vertexConsumers, int light, Player player, float limbSwing, float limbSwingAmount, float partialTick, float animationProgress, float headYaw, float headPitch) {
        this.currentPlayer = player;
        StyleData styleData = StyleData.getEntityData(player);

        poseStack.translate(0.0D, 1.497F, 0.0D);
        poseStack.scale(-1.005F, -1.0F, 1.005F);
        poseStack.pushPose();

        renderStyle(poseStack, styleData.getHeadStyle(), Category.Head, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getBodyStyle(), Category.Body, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getLegsStyle(), Category.Legs, vertexConsumers, headYaw, partialTick, light, false);
        renderStyle(poseStack, styleData.getFeetStyle(), Category.Feet, vertexConsumers, headYaw, partialTick, light, false);

        poseStack.popPose();
        poseStack.scale(-1.005F, -1.0F, 1.005F);
        poseStack.translate(0.0D, -1.497F, 0.0D);

        this.currentPlayer = null;
    }

    public void renderStyle(PoseStack poseStack, EquippedStyle equippedStyle, Category category, MultiBufferSource vertexConsumers, float headYaw, float partialTick, int light, boolean isGui) {
        if (equippedStyle.getStyle().isEmpty()) return;
        renderStyle(poseStack, equippedStyle.getStyle().get(), equippedStyle.getVariant(), category, vertexConsumers, headYaw, partialTick, light, isGui);
    }

    public void renderStyle(PoseStack poseStack, Style style, int variant, Category category, MultiBufferSource bufferSource, float headYaw, float partialTick, int light, boolean isGui) {
        Identifier texture = getStyleTexture(style, variant);

        RenderType renderLayer = getRenderType(style, texture, bufferSource, partialTick);
        var bakedModel = geoModel.getBakedModel(this.getGeoModel().getModelResource(style, this));
        setupBoneVisibility(bakedModel, category);
        fitToBones(bakedModel, category);
        defaultRender(poseStack, style, bufferSource, renderLayer, null, headYaw, partialTick, light);
    }

    public void renderStyleForGUI(PoseStack poseStack, Style style, int textureId, Category category, MultiBufferSource bufferSource, float headYaw, float partialTick) {
        Identifier texture = getStyleTexture(style, textureId);

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
        //software.bernie.geckolib.util.RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
        //? } else {
        software.bernie.geckolib.util.RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
        //? }
    }

    //? if <= 1.20.1 {
//    @Override
//    public void actuallyRender(PoseStack poseStack, Style style, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
//        setupAnimation(style, isReRender, partialTick);
//        GeoRenderer.super.actuallyRender(poseStack, style, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    //? } else {
    @Override
    public void actuallyRender(PoseStack poseStack, Style style, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        setupAnimation(style, isReRender, partialTick);
        GeoRenderer.super.actuallyRender(poseStack, style, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
    //? }


    @Override
    public void applyRenderLayers(PoseStack poseStack, Style animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        for (GeoRenderLayer<Style> renderLayer : getRenderLayers()) {
            if (renderLayer instanceof AutoGlowingGeoLayer) {
                var emissiveTexture = this.getTextureLocation(animatable).withPath(path -> path.replace(".png", "_glowmask.png"));

                if (emissiveCache.computeIfAbsent(emissiveTexture, id -> Minecraft.getInstance().getResourceManager().getResource(id).isPresent()))
                    renderLayer.render(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
                else
                    continue;
            }
            renderLayer.render(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        }
    }

    private static Identifier getStyleTexture(Style style, int textureId) {
        Identifier texture = style.getTextureId();
        if (textureId >= 0 && style.getTextureVariants().isPresent()) {
            var textures = style.getTextureVariants().get();
            if (textures.size() > textureId) texture = textures.get(textureId);
        }
        return texture;
    }

    private void setupAnimation(Style style, boolean isReRender, float partialTick) {
        if (this.currentPlayer != null && !isReRender) {
            boolean isMoving = false;
            long instanceId = -1;

            if (currentPlayer != null) {
                double i = Math.abs(currentPlayer.getX() - currentPlayer.xOld) + Math.abs(currentPlayer.getZ() - currentPlayer.zOld);
                isMoving = i > 0.075 && currentPlayer.walkAnimation.isMoving();
                instanceId = currentPlayer.getId();
            }

            AnimationState<Style> animationState = new AnimationState<>(style, 0, 0, partialTick, isMoving);

            animationState.setData(Style.PLAYER, this.currentPlayer);
            animationState.setData(AnimationHandler.TICKET_ANIM_STATE, getAnimSate(isMoving));
            geoModel.addAdditionalStateData(style, instanceId, animationState::setData);
            //? if <= 1.20.1 {
            //geoModel.handleAnimations(style, instanceId, animationState);
            //? } else {
            geoModel.handleAnimations(style, instanceId, animationState, partialTick);
            //? }
        }
    }

    private AnimState getAnimSate(boolean isMoving) {
        if (currentPlayer.isSleeping())
            return AnimState.SLEEPING;

        else if (currentPlayer.isInLiquid() && currentPlayer.isVisuallySwimming())
            return AnimState.SWIMMING;

        else if (currentPlayer.isFallFlying())
            return AnimState.FLYING;

        else if (!currentPlayer.onGround())
            return AnimState.IN_AIR;

        else if (currentPlayer.isCrouching())
            return AnimState.SNEAKING;

        else if (isMoving) {
            if (currentPlayer.isSprinting())
                return AnimState.SPRINTING;
            else
                return AnimState.WALKING;
        }
        else
            return AnimState.IDLE;
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
        PlayerModel playerModel = getParentModel();

        var renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.currentPlayer);
        if ((Object) renderer instanceof PlayerRenderer playerRenderer) {
            playerModel = playerRenderer.getModel();
        }

        switch (category) {
            case Head -> {
                GeoBone bone = model.getBone(headBone).orElse(null);
                if (bone != null) {
                    matchModelPartRot(playerModel.head, bone);
                    bone.updatePosition(playerModel.head.x, -playerModel.head.y, playerModel.head.z);
                }
            }
            case Body -> {
                GeoBone bodyGeoBone = model.getBone(bodyBone).orElse(null);
                if (bodyGeoBone != null) {
                    matchModelPartRot(playerModel.body, bodyGeoBone);
                    bodyGeoBone.updatePosition(playerModel.body.x, -playerModel.body.y, playerModel.body.z);
                }

                GeoBone rightArmGeoBone = model.getBone(rightArmBone).orElse(null);
                GeoBone leftArmGeoBone = model.getBone(leftArmBone).orElse(null);
                if (bodyGeoBone != null && rightArmGeoBone != null && leftArmGeoBone != null) {
                    matchModelPartRot(playerModel.rightArm, rightArmGeoBone);
                    matchModelPartRot(playerModel.leftArm, leftArmGeoBone);
                    rightArmGeoBone.setModelPosition(new Vector3d(playerModel.rightArm.x + 5, 2 - playerModel.rightArm.y, playerModel.rightArm.z));
                    leftArmGeoBone.setModelPosition(new Vector3d(playerModel.leftArm.x - 5, 2 - playerModel.leftArm.y, playerModel.leftArm.z));
                }
            }
            case Legs -> {
                GeoBone rightLegGeoBone = model.getBone(rightLegBone).orElse(null);
                GeoBone leftLegGeoBone = model.getBone(leftLegBone).orElse(null);
                if (rightLegGeoBone != null && leftLegGeoBone != null) {
                    matchModelPartRot(playerModel.rightLeg, rightLegGeoBone);
                    matchModelPartRot(playerModel.leftLeg, leftLegGeoBone);
                    rightLegGeoBone.setModelPosition(new Vector3d(playerModel.rightLeg.x + 2, 12 - playerModel.rightLeg.y, playerModel.rightLeg.z));
                    leftLegGeoBone.setModelPosition(new Vector3d(playerModel.leftLeg.x - 2, 12 - playerModel.leftLeg.y, playerModel.leftLeg.z));
                }
            }
            case Feet -> {
                GeoBone rightBootGeoBone = model.getBone(rightBootBone).orElse(null);
                GeoBone leftBootGeoBone = model.getBone(leftBootBone).orElse(null);
                if (rightBootGeoBone != null && leftBootGeoBone != null) {
                    matchModelPartRot(playerModel.rightLeg, rightBootGeoBone);
                    matchModelPartRot(playerModel.leftLeg, leftBootGeoBone);
                    rightBootGeoBone.setModelPosition(new Vector3d(playerModel.rightLeg.x + 2, 12 - playerModel.rightLeg.y, playerModel.rightLeg.z));
                    leftBootGeoBone.setModelPosition(new Vector3d(playerModel.leftLeg.x - 2, 12 - playerModel.leftLeg.y, playerModel.leftLeg.z));
                }
            }
        }
    }

    private void matchModelPartRot(ModelPart modelPart, GeoBone geoBone) {
        //? if <= 1.20.1 {
        //software.bernie.geckolib.util.RenderUtils.matchModelPartRot(modelPart, geoBone);
        //? } else {
        software.bernie.geckolib.util.RenderUtil.matchModelPartRot(modelPart, geoBone);
        //? }
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
    public List<GeoRenderLayer<Style>> getRenderLayers() {
        return renderLayers;
    }

    @Override
    public GeoModel<Style> getGeoModel() {
        return geoModel;
    }

    @Override
    public void fireCompileRenderLayersEvent() {}

    @Override
    public RenderType getRenderType(Style animatable, Identifier texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutout(texture);
    }

    @Override
    public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {
        return true;
    }

    @Override
    public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel model, MultiBufferSource bufferSource, float partialTick, int packedLight) {}

    @Override
    public void updateAnimatedTextureFrame(Style animatable) {}

    @Override
    public Style getAnimatable() {
        return null;
    }
}
*///? }