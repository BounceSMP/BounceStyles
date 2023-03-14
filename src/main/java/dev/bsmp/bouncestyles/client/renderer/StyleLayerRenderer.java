package dev.bsmp.bouncestyles.client.renderer;

import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.BounceStylesClient;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleData;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.GeoUtils;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.List;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class StyleLayerRenderer extends FeatureRenderer<PlayerEntity, PlayerEntityModel<PlayerEntity>> implements IGeoRenderer<Style> {
    protected VertexConsumerProvider rtb = null;
    private final StyleModel modelProvider;

    public static String headBone = "armorHead";
    public static String bodyBone = "armorBody";
    public static String rightArmBone = "armorRightArm";
    public static String leftArmBone = "armorLeftArm";
    public static String rightLegBone = "armorRightLeg";
    public static String leftLegBone = "armorLeftLeg";
    public static String rightBootBone = "armorRightBoot";
    public static String leftBootBone = "armorLeftBoot";

    static {
        AnimationController.addModelFetcher(animatable -> {
            if(animatable instanceof Style) {
                return (IAnimatableModel) BounceStylesClient.STYLE_RENDERER.getGeoModelProvider();
            }
            return null;
        });
    }

    public StyleLayerRenderer(FeatureRendererContext<PlayerEntity, PlayerEntityModel<PlayerEntity>> renderer) {
        super(renderer);
        this.modelProvider = new StyleModel();
    }

    @Override
    public void render(MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight, PlayerEntity player, float limbSwing, float limbSwingAmount,  float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        StyleData styleData = StyleData.getPlayerData(player);

        poseStack.translate(0.0D, 1.497F, 0.0D);
        poseStack.scale(-1.005F, -1.0F, 1.005F);
        poseStack.push();

        if(styleData.getHeadStyle() != null)
            renderStyle(poseStack, styleData.getHeadStyle(), player, StyleLoader.Category.Head, buffer, limbSwing, limbSwingAmount, partialTick, packedLight);
        if(styleData.getBodyStyle() != null)
            renderStyle(poseStack, styleData.getBodyStyle(), player, StyleLoader.Category.Body, buffer, limbSwing, limbSwingAmount, partialTick, packedLight);
        if(styleData.getLegStyle() != null)
            renderStyle(poseStack, styleData.getLegStyle(), player, StyleLoader.Category.Legs, buffer, limbSwing, limbSwingAmount, partialTick, packedLight);
        if(styleData.getFeetStyle() != null)
            renderStyle(poseStack, styleData.getFeetStyle(), player, StyleLoader.Category.Feet, buffer, limbSwing, limbSwingAmount, partialTick, packedLight);

        poseStack.pop();
        poseStack.scale(-1.005F, -1.0F, 1.005F);
        poseStack.translate(0.0D, -1.497F, 0.0D);
    }

    public void renderStyle(MatrixStack poseStack, Style style, PlayerEntity player, StyleLoader.Category category, VertexConsumerProvider buffer, float limbSwing, float limbSwingAmount, float partialTick, int packedLight) {
        if(GeckoLibCache.getInstance().getGeoModels().containsKey(style.modelID)) {
            GeoModel model = this.modelProvider.getModel(style.modelID);
            this.modelProvider.setCustomAnimations(style, getInstanceId(style), new AnimationEvent<>(style, limbSwing, limbSwingAmount, partialTick, false, List.of(player)));
            fit(poseStack, category, false);
            RenderLayer renderType = getRenderType(style, partialTick, poseStack, buffer, null, packedLight, getTextureLocation(style));
            render(model, style, partialTick, renderType, poseStack, buffer, null, packedLight, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
        }
    }

    public void renderStyleForGUI(MatrixStack poseStack, Style style, StyleLoader.Category category, VertexConsumerProvider buffer, float partialTick, int packedLight) {
        GeoModel model = this.modelProvider.getModel(style.modelID);
        fit(poseStack, category, true);
        RenderLayer renderType = getRenderType(style, partialTick, poseStack, buffer, null, packedLight, getTextureLocation(style));
        render(model, style, partialTick, renderType, poseStack, buffer, null, packedLight, OverlayTexture.DEFAULT_UV, 1f, 1f, 1f, 1f);
    }

    private void fit(MatrixStack poseStack, StyleLoader.Category category, boolean gui) {
        setBoneVisibility(headBone, false);
        setBoneVisibility(bodyBone, false);
        setBoneVisibility(rightArmBone, false);
        setBoneVisibility(leftArmBone, false);
        setBoneVisibility(rightLegBone, false);
        setBoneVisibility(leftLegBone, false);
        setBoneVisibility(rightBootBone, false);
        setBoneVisibility(rightBootBone, false);
        setBoneVisibility(leftBootBone, false);
        PlayerEntityModel<PlayerEntity> playerModel = getContextModel();

        switch (category) {
            case Head -> {
                GeoBone bone = (GeoBone) this.modelProvider.getBone(headBone);
                if(!gui)
                    GeoUtils.copyRotations(getContextModel().head, bone);
                else
                    RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
                setBoneVisibility(headBone, true);
                bone.setPosition(playerModel.head.pivotX, -playerModel.head.pivotY, playerModel.head.pivotZ);
            }
            case Body -> {
                GeoBone bodyGeoBone = (GeoBone) this.modelProvider.getBone(bodyBone);
                GeoBone rightArmGeoBone = (GeoBone) this.modelProvider.getBone(rightArmBone);
                GeoBone leftArmGeoBone = (GeoBone) this.modelProvider.getBone(leftArmBone);
                if(!gui) {
                    GeoUtils.copyRotations(getContextModel().body, bodyGeoBone);
                    GeoUtils.copyRotations(getContextModel().rightArm, rightArmGeoBone);
                    GeoUtils.copyRotations(getContextModel().leftArm, leftArmGeoBone);
                }
                else {
                    RenderUtils.translateAwayFromPivotPoint(poseStack, bodyGeoBone);
                }
                setBoneVisibility(bodyBone, true);
                setBoneVisibility(rightArmBone, true);
                setBoneVisibility(leftArmBone, true);
                bodyGeoBone.setPosition(playerModel.body.pivotX, -playerModel.body.pivotY, playerModel.body.pivotZ);
                rightArmGeoBone.setPosition(playerModel.rightArm.pivotX + 5, 2 - playerModel.rightArm.pivotY, playerModel.rightArm.pivotZ);
                leftArmGeoBone.setPosition(playerModel.leftArm.pivotX - 5, 2 - playerModel.leftArm.pivotY, playerModel.leftArm.pivotZ);
            }
            case Legs -> {
                GeoBone rightLegGeoBone = (GeoBone) this.modelProvider.getBone(rightLegBone);
                GeoBone leftLegGeoBone = (GeoBone) this.modelProvider.getBone(leftLegBone);
                if(!gui) {
                    GeoUtils.copyRotations(getContextModel().rightLeg, rightLegGeoBone);
                    GeoUtils.copyRotations(getContextModel().leftLeg, leftLegGeoBone);
                }
                else {
                    RenderUtils.translateAwayFromPivotPoint(poseStack, rightLegGeoBone);
                    RenderUtils.translateAwayFromPivotPoint(poseStack, leftLegGeoBone);
                }
                setBoneVisibility(rightLegBone, true);
                setBoneVisibility(leftLegBone, true);
                rightLegGeoBone.setPosition(playerModel.rightLeg.pivotX + 2, 12 - playerModel.rightLeg.pivotY, playerModel.rightLeg.pivotZ);
                leftLegGeoBone.setPosition(playerModel.leftLeg.pivotX - 2, 12 - playerModel.leftLeg.pivotY, playerModel.leftLeg.pivotZ);
            }
            case Feet -> {
                GeoBone rightBootGeoBone = (GeoBone) this.modelProvider.getBone(rightBootBone);
                GeoBone leftBootGeoBone = (GeoBone) this.modelProvider.getBone(leftBootBone);
                if(!gui) {
                    GeoUtils.copyRotations(getContextModel().rightLeg, rightBootGeoBone);
                    GeoUtils.copyRotations(getContextModel().leftLeg, leftBootGeoBone);
                }
                else {
                    RenderUtils.translateAwayFromPivotPoint(poseStack, rightBootGeoBone);
                    RenderUtils.translateAwayFromPivotPoint(poseStack, leftBootGeoBone);
                }
                setBoneVisibility(rightBootBone, true);
                setBoneVisibility(leftBootBone, true);
                rightBootGeoBone.setPosition(playerModel.rightLeg.pivotX + 2, 12 - playerModel.rightLeg.pivotY, playerModel.rightLeg.pivotZ);
                leftBootGeoBone.setPosition(playerModel.leftLeg.pivotX - 2, 12 - playerModel.leftLeg.pivotY, playerModel.leftLeg.pivotZ);
            }
        }
    }

    private void setBoneVisibility(String bone, boolean isVisible) {
        this.modelProvider.getBone(bone).setHidden(!isVisible);
    }

    @Override
    public void setCurrentRTB(VertexConsumerProvider bufferSource) {
        this.rtb = bufferSource;
    }

    @Override
    public VertexConsumerProvider getCurrentRTB() {
        return this.rtb;
    }

    @Override
    public AnimatedGeoModel<Style> getGeoModelProvider() {
        return this.modelProvider;
    }

    @Override
    public Identifier getTextureLocation(Style style) {
        return this.modelProvider.getTextureLocation(style);
    }
}
