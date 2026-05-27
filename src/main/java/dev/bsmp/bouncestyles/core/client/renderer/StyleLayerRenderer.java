package dev.bsmp.bouncestyles.core.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.data.Category;
import dev.bsmp.bouncestyles.core.data.EquippedStyle;
import dev.bsmp.bouncestyles.core.data.Style;
import dev.bsmp.bouncestyles.core.data.StyleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.EquipmentSlot;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class StyleLayerRenderer extends RenderLayer<AvatarRenderState, PlayerModel> implements GeoRenderer<Style, StyleData, GeoRenderState.Impl> {
    public static final StyleGeoModel geoModel = new StyleGeoModel();
    public static final DataTicket<EquippedStyle> TICKET_STYLE = DataTicket.create("style", EquippedStyle.class);
    public static final DataTicket<Category> TICKET_CATEGORY = DataTicket.create("style_category", Category.class);
    public static final DataTicket<StyleData> TICKET_STYLE_DATA = DataTicket.create("style_data", StyleData.class);

    public StyleLayerRenderer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, AvatarRenderState avatarState, float yRot, float xRot) {
        var cameraState = Minecraft.getInstance().gameRenderer.getLevelRenderState().cameraRenderState;

        var styleData = avatarState.getGeckolibData(TICKET_STYLE_DATA);
        if (styleData != null) {
            styleData.getAllNonEmpty().forEach((category, equippedStyle) -> {
                var renderState = createRenderState(equippedStyle.getStyle().get(), styleData);
                renderState.addGeckolibData(TICKET_STYLE, equippedStyle);
                renderState.addGeckolibData(TICKET_CATEGORY, category);
                GeoRenderer.super.performRenderPass(renderState, poseStack, submitNodeCollector, cameraState);
            });
        }
    }

    @Override
    public void submitRenderTasks(RenderPassInfo<GeoRenderState.Impl> renderPassInfo, OrderedSubmitNodeCollector renderTasks, @Nullable RenderType renderType) {
        if (renderType == null) return;

        final int packedLight = renderPassInfo.packedLight();
        final int packedOverlay = renderPassInfo.packedOverlay();
        final int renderColor = renderPassInfo.renderColor();
        final GeoRenderState renderState = renderPassInfo.renderState();
        final Category category = Objects.requireNonNull(renderState.getGeckolibData(TICKET_CATEGORY));

        renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, vertexConsumer) -> {
            final PoseStack poseStack = renderPassInfo.poseStack();

            poseStack.pushPose();
            poseStack.last().set(pose);
            renderPassInfo.renderPosed(() -> {
                for (GeoArmorRenderer.ArmorSegment segment : getSegmentsForCategory(category)) {
                    renderPassInfo.model().getBone(getBoneNameForSegment(segment))
                            .ifPresent(bone -> bone.positionAndRender(renderPassInfo, vertexConsumer, packedLight, packedOverlay, renderColor));
                }
            });
            poseStack.popPose();
        });
    }

    @Override
    public void adjustRenderPose(@NonNull RenderPassInfo<GeoRenderState.Impl> renderPassInfo) {
        renderPassInfo.poseStack().translate(0, 24 / 16f, 0);
        renderPassInfo.poseStack().scale(-1, -1, 1);
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<GeoRenderState.Impl> renderPassInfo, BoneSnapshots snapshots) {
        var category = renderPassInfo.renderState().getGeckolibData(TICKET_CATEGORY);

        getSegmentsForCategory(category).forEach(segment ->
                snapshots.get(getBoneNameForSegment(segment)).ifPresent(boneSnapshot -> {
                    final ModelPart modelPart = segment.modelPartGetter.apply(getParentModel());
                    final Vector3f bonePos = segment.modelPartMatcher.apply(new Vector3f(modelPart.x, modelPart.y, modelPart.z));

                    boneSnapshot.setRotX(-modelPart.xRot)
                            .setRotY(-modelPart.yRot)
                            .setRotZ(modelPart.zRot)
                            .setTranslateX(bonePos.x)
                            .setTranslateY(bonePos.y)
                            .setTranslateZ(bonePos.z);
                })
        );
    }

    @Override
    public boolean firePreRenderEvent(@NonNull RenderPassInfo<GeoRenderState.Impl> renderPassInfo, @NonNull SubmitNodeCollector renderTasks) {
        return true;
    }

    @Override
    public @NonNull GeoModel<Style> getGeoModel() {
        return geoModel;
    }

    @Override
    public @Nullable RenderType getRenderType(GeoRenderState.Impl renderState, Identifier texture) {
        //ToDo Allow choosing rendertype per-style?
        return RenderTypes.entityCutoutNoCull(texture);
    }

    @Override
    public GeoRenderState.Impl createRenderState(Style style, @Nullable StyleData styleData) {
        return new GeoRenderState.Impl();
    }

    @Override
    public void fireCompileRenderLayersEvent() {}

    @Override
    public void fireCompileRenderStateEvent(Style animatable, @Nullable StyleData styleData, GeoRenderState.Impl renderState, float partialTick) {}

    public List<GeoArmorRenderer.ArmorSegment> getSegmentsForCategory(Category category) {
        return switch (category) {
            case Head -> List.of(GeoArmorRenderer.ArmorSegment.HEAD);
            case Body -> List.of(GeoArmorRenderer.ArmorSegment.CHEST, GeoArmorRenderer.ArmorSegment.LEFT_ARM, GeoArmorRenderer.ArmorSegment.RIGHT_ARM);
            case Legs -> List.of(GeoArmorRenderer.ArmorSegment.LEFT_LEG, GeoArmorRenderer.ArmorSegment.RIGHT_LEG);
            case Feet -> List.of(GeoArmorRenderer.ArmorSegment.LEFT_FOOT, GeoArmorRenderer.ArmorSegment.RIGHT_FOOT);
        };
    }

    public String getBoneNameForSegment(GeoArmorRenderer.ArmorSegment segment) {
        return switch (segment) {
            case HEAD -> "armorHead";
            case CHEST -> "armorBody";
            case LEFT_ARM -> "armorLeftArm";
            case RIGHT_ARM -> "armorRightArm";
            case LEFT_LEG -> "armorLeftLeg";
            case RIGHT_LEG -> "armorRightLeg";
            case LEFT_FOOT -> "armorLeftBoot";
            case RIGHT_FOOT -> "armorRightBoot";
        };
    }

    //    @Override
//    public void addRenderData(Style animatable, @Nullable Void relatedObject, StyleRenderState renderState, float partialTick) {
//        GeoRenderer.super.addRenderData(animatable, relatedObject, renderState, partialTick);
//    }
//
//    @Override
//    public void render(PoseStack poseStack, MultiBufferSource vertexConsumers, int light, Player player, float limbAngle, float limbDistance, float partialTick, float animationProgress, float headYaw, float headPitch) {
//        this.currentPlayer = player;
//        StyleData styleData = StyleData.getOrCreateStyleData(player);
//
//        poseStack.translate(0.0D, 1.497F, 0.0D);
//        poseStack.scale(-1.005F, -1.0F, 1.005F);
//        poseStack.pushPose();
//
//        renderStyle(poseStack, styleData.getHeadStyle(), Category.Head, vertexConsumers, headYaw, partialTick, light, false);
//        renderStyle(poseStack, styleData.getBodyStyle(), Category.Body, vertexConsumers, headYaw, partialTick, light, false);
//        renderStyle(poseStack, styleData.getLegStyle(), Category.Legs, vertexConsumers, headYaw, partialTick, light, false);
//        renderStyle(poseStack, styleData.getFeetStyle(), Category.Feet, vertexConsumers, headYaw, partialTick, light, false);
//
//        poseStack.popPose();
//        poseStack.scale(-1.005F, -1.0F, 1.005F);
//        poseStack.translate(0.0D, -1.497F, 0.0D);
//    }
//
//    public void renderStyle(PoseStack poseStack, Optional<Pair<Style, Integer>> equippedStyle, Category category, MultiBufferSource vertexConsumers, float headYaw, float partialTick, int light, boolean isGui) {
//        if (equippedStyle.isEmpty()) return;
//        renderStyle(poseStack, equippedStyle.get().getFirst(), equippedStyle.get().getSecond(), category, vertexConsumers, headYaw, partialTick, light, isGui);
//    }
//
//    public void renderStyle(PoseStack poseStack, Style style, int textureId, Category category, MultiBufferSource bufferSource, float headYaw, float partialTick, int light, boolean isGui) {
//        Identifier texture = getStyleTexture(style, textureId);
//
//        RenderType renderLayer = getRenderType(style, texture, bufferSource, partialTick);
//        var bakedModel = geoModel.getBakedModel(style.getModelId());
//        setupBoneVisibility(bakedModel, category);
//        fitToBones(bakedModel, category);
//        defaultRender(poseStack, style, bufferSource, renderLayer, null, headYaw, partialTick, light);
//    }
//
//    public void renderStyleForGUI(PoseStack poseStack, Style style, int textureId, Category category, MultiBufferSource bufferSource, float headYaw, float partialTick) {
//        Identifier texture = getStyleTexture(style, textureId);
//
//        RenderType renderType = getRenderType(style, texture, bufferSource, partialTick);
//        VertexConsumer buffer = bufferSource.getBuffer(renderType);
//
//        var bakedModel = geoModel.getBakedModel(style.getModelId());
//        setupBoneVisibility(bakedModel, category);
//
//        poseStack.pushPose();
//
//        switch (category) {
//            case Head -> {
//                moveFromPivot(poseStack, bakedModel.getBone(headBone).get());
//                poseStack.translate(0, -.7f, 0);
//            }
//            case Body -> moveFromPivot(poseStack, bakedModel.getBone(bodyBone).get());
//            case Legs -> {
//                moveFromPivot(poseStack, bakedModel.getBone(leftLegBone).get());
//                poseStack.translate(.1f, 0, 0);
//            }
//            case Feet -> {
//                moveFromPivot(poseStack, bakedModel.getBone(leftBootBone).get());
//                poseStack.translate(.1f, 0, 0);
//            }
//        }
//
//        poseStack.translate(0, 1.1f, 0);
//        defaultRender(poseStack, style, bufferSource, renderType, buffer, 0f, partialTick, 15728880);
//
//        poseStack.popPose();
//    }
//
//    private void moveFromPivot(PoseStack poseStack, GeoBone bone) {
//        software.bernie.geckolib.util.RenderUtil.translateAwayFromPivotPoint(poseStack, bone);
//    }
//
//    @Override
//    public void actuallyRender(PoseStack poseStack, Style style, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
//        setupAnimation(style, isReRender, partialTick);
//        GeoRenderer.super.actuallyRender(poseStack, style, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
//    }
//
//    private static Identifier getStyleTexture(Style style, int textureId) {
//        Identifier texture = style.getTextureId();
//        if (textureId >= 0 && style.getTextureVariants().isPresent()) {
//            var textures = style.getTextureVariants().get();
//            if (textures.size() > textureId) texture = textures.get(textureId);
//        }
//        return texture;
//    }
//
//    private void setupAnimation(Style style, boolean isReRender, float partialTick) {
//        if (!isReRender) {
//            boolean isMoving = false;
//            long instanceId = -1;
//
//            if (currentPlayer != null) {
//                double i = Math.abs(currentPlayer.getX() - currentPlayer.xOld) + Math.abs(currentPlayer.getZ() - currentPlayer.zOld);
//                isMoving = i > 0.075 && currentPlayer.walkAnimation.isMoving();
//                instanceId = currentPlayer.getId();
//            }
//
//            AnimationState<Style> animationState = new AnimationState<>(style, 0, 0, partialTick, isMoving);
//
//            animationState.setData(Style.PLAYER, this.currentPlayer);
//            geoModel.addAdditionalStateData(style, instanceId, animationState::setData);
//            //? if <= 1.20.1 {
//            /*geoModel.handleAnimations(style, instanceId, animationState);
//            *///?} else if >= 1.21.1 {
//            geoModel.handleAnimations(style, instanceId, animationState, partialTick);
//            //?}
//        }
//    }
//
//    private void setupBoneVisibility(BakedGeoModel model, Category category) {
//        setBoneVisibility(headBone, model, false);
//        setBoneVisibility(bodyBone, model, false);
//        setBoneVisibility(rightArmBone, model, false);
//        setBoneVisibility(leftArmBone, model, false);
//        setBoneVisibility(rightLegBone, model, false);
//        setBoneVisibility(leftLegBone, model, false);
//        setBoneVisibility(rightBootBone, model, false);
//        setBoneVisibility(rightBootBone, model, false);
//        setBoneVisibility(leftBootBone, model, false);
//
//        switch (category) {
//            case Head -> setBoneVisibility(headBone, model, true);
//            case Body -> {
//                setBoneVisibility(bodyBone, model, true);
//                setBoneVisibility(rightArmBone, model, true);
//                setBoneVisibility(leftArmBone, model, true);
//            }
//            case Legs -> {
//                setBoneVisibility(rightLegBone, model, true);
//                setBoneVisibility(leftLegBone, model, true);
//            }
//            case Feet -> {
//                setBoneVisibility(rightBootBone, model, true);
//                setBoneVisibility(leftBootBone, model, true);
//            }
//        }
//    }
//
//    private void fitToBones(BakedGeoModel model, Category category) {
//        PlayerModel<Player> playerModel = getParentModel();
//
//        switch (category) {
//            case Head -> {
//                GeoBone bone = model.getBone(headBone).orElse(null);
//                if (bone != null) {
//                    matchModelPartRot(getParentModel().head, bone);
//                    bone.setModelPosition(new Vector3d(playerModel.head.x, -playerModel.head.y, playerModel.head.z));
//                }
//            }
//            case Body -> {
//                GeoBone bodyGeoBone = model.getBone(bodyBone).orElse(null);
//                GeoBone rightArmGeoBone = model.getBone(rightArmBone).orElse(null);
//                GeoBone leftArmGeoBone = model.getBone(leftArmBone).orElse(null);
//                if (bodyGeoBone != null && rightArmGeoBone != null && leftArmGeoBone != null) {
//                    matchModelPartRot(getParentModel().body, bodyGeoBone);
//                    matchModelPartRot(getParentModel().rightArm, rightArmGeoBone);
//                    matchModelPartRot(getParentModel().leftArm, leftArmGeoBone);
//                    bodyGeoBone.setModelPosition(new Vector3d(playerModel.body.x, -playerModel.body.y, playerModel.body.z));
//                    rightArmGeoBone.setModelPosition(new Vector3d(playerModel.rightArm.x + 5, 2 - playerModel.rightArm.y, playerModel.rightArm.z));
//                    leftArmGeoBone.setModelPosition(new Vector3d(playerModel.leftArm.x - 5, 2 - playerModel.leftArm.y, playerModel.leftArm.z));
//                }
//            }
//            case Legs -> {
//                GeoBone rightLegGeoBone = model.getBone(rightLegBone).orElse(null);
//                GeoBone leftLegGeoBone = model.getBone(leftLegBone).orElse(null);
//                if (rightLegGeoBone != null && leftLegGeoBone != null) {
//                    matchModelPartRot(getParentModel().rightLeg, rightLegGeoBone);
//                    matchModelPartRot(getParentModel().leftLeg, leftLegGeoBone);
//                    rightLegGeoBone.setModelPosition(new Vector3d(playerModel.rightLeg.x + 2, 12 - playerModel.rightLeg.y, playerModel.rightLeg.z));
//                    leftLegGeoBone.setModelPosition(new Vector3d(playerModel.leftLeg.x - 2, 12 - playerModel.leftLeg.y, playerModel.leftLeg.z));
//                }
//            }
//            case Feet -> {
//                GeoBone rightBootGeoBone = model.getBone(rightBootBone).orElse(null);
//                GeoBone leftBootGeoBone = model.getBone(leftBootBone).orElse(null);
//                if (rightBootGeoBone != null && leftBootGeoBone != null) {
//                    matchModelPartRot(getParentModel().rightLeg, rightBootGeoBone);
//                    matchModelPartRot(getParentModel().leftLeg, leftBootGeoBone);
//                    rightBootGeoBone.setModelPosition(new Vector3d(playerModel.rightLeg.x + 2, 12 - playerModel.rightLeg.y, playerModel.rightLeg.z));
//                    leftBootGeoBone.setModelPosition(new Vector3d(playerModel.leftLeg.x - 2, 12 - playerModel.leftLeg.y, playerModel.leftLeg.z));
//                }
//            }
//        }
//    }
//
//    private void matchModelPartRot(ModelPart modelPart, GeoBone geoBone) {
//        software.bernie.geckolib.util.RenderUtil.matchModelPartRot(modelPart, geoBone);
//    }
//
//    private void setBoneVisibility(String bone, BakedGeoModel model, boolean isVisible) {
//        try {
//            model.getBone(bone).ifPresent(geoBone -> geoBone.setHidden(!isVisible));
//        }
//        catch (RuntimeException e) {
//            BounceStyles.LOGGER.info("Could not find bone ["+bone+"]");
//        }
//    }
//
//    @Override
//    public void fireCompileRenderLayersEvent() {}
//
//    @Override
//    public void fireCompileRenderStateEvent(Style animatable, @Nullable Void relatedObject, GeoRenderState renderState, float partialTick) {
//
//    }
//
//    @Override
//    public boolean firePreRenderEvent(RenderPassInfo renderPassInfo, SubmitNodeCollector renderTasks) {
//        return false;
//    }
}
