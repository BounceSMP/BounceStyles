package dev.bsmp.bouncestyles.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.GarmentLoader;
import dev.bsmp.bouncestyles.client.BounceStylesClient;
import dev.bsmp.bouncestyles.data.Garment;
import dev.bsmp.bouncestyles.data.PlayerStyleData;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.GeoUtils;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.List;

public class GarmentLayerRenderer extends RenderLayer<Player, PlayerModel<Player>> implements IGeoRenderer<Garment> {
    protected MultiBufferSource rtb = null;
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
            if(animatable instanceof Garment) {
                return (IAnimatableModel) BounceStylesClient.GARMENT_RENDERER.getGeoModelProvider();
            }
            return null;
        });
    }

    public GarmentLayerRenderer(RenderLayerParent<Player, PlayerModel<Player>> renderer) {
        super(renderer);
        this.modelProvider = new StyleModel();
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Player player, float limbSwing, float limbSwingAmount,  float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        PlayerStyleData styleData = PlayerStyleData.getPlayerData(player);


        poseStack.translate(0.0D, 1.497F, 0.0D);
        poseStack.scale(-1.005F, -1.0F, 1.005F);
        poseStack.pushPose();

        if(styleData.getHeadGarment() != null)
            renderGarment(poseStack, styleData.getHeadGarment(), player, GarmentLoader.Category.Head, buffer, limbSwing, limbSwingAmount, partialTick, packedLight);
        if(styleData.getBodyGarment() != null)
            renderGarment(poseStack, styleData.getBodyGarment(), player, GarmentLoader.Category.Body, buffer, limbSwing, limbSwingAmount, partialTick, packedLight);
        if(styleData.getLegGarment() != null)
            renderGarment(poseStack, styleData.getLegGarment(), player, GarmentLoader.Category.Legs, buffer, limbSwing, limbSwingAmount, partialTick, packedLight);
        if(styleData.getFeetGarment() != null)
            renderGarment(poseStack, styleData.getFeetGarment(), player, GarmentLoader.Category.Feet, buffer, limbSwing, limbSwingAmount, partialTick, packedLight);

        poseStack.popPose();
        poseStack.scale(-1.005F, -1.0F, 1.005F);
        poseStack.translate(0.0D, -1.497F, 0.0D);
    }

    public void renderGarment(PoseStack poseStack, Garment garment, Player player, GarmentLoader.Category category, MultiBufferSource buffer, float limbSwing, float limbSwingAmount, float partialTick, int packedLight) {
        if(GeckoLibCache.getInstance().getGeoModels().containsKey(garment.modelID)) {
            GeoModel model = this.modelProvider.getModel(garment.modelID);
            this.modelProvider.setCustomAnimations(garment, getInstanceId(garment), new AnimationEvent<>(garment, limbSwing, limbSwingAmount, partialTick, false, List.of(player)));
            fit(poseStack, category, false);
            RenderType renderType = getRenderType(garment, partialTick, poseStack, buffer, null, packedLight, getTextureLocation_geckolib(garment));
            render(model, garment, partialTick, renderType, poseStack, buffer, null, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }
    }

    public void renderGarmentForGUI(PoseStack poseStack, Garment garment, GarmentLoader.Category category, MultiBufferSource buffer, float partialTick, int packedLight) {
        GeoModel model = this.modelProvider.getModel(garment.modelID);
        fit(poseStack, category, true);
        RenderType renderType = getRenderType(garment, partialTick, poseStack, buffer, null, packedLight, getTextureLocation_geckolib(garment));
        render(model, garment, partialTick, renderType, poseStack, buffer, null, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private void fit(PoseStack poseStack, GarmentLoader.Category category, boolean gui) {
        setBoneVisibility(headBone, false);
        setBoneVisibility(bodyBone, false);
        setBoneVisibility(rightArmBone, false);
        setBoneVisibility(leftArmBone, false);
        setBoneVisibility(rightLegBone, false);
        setBoneVisibility(leftLegBone, false);
        setBoneVisibility(rightBootBone, false);
        setBoneVisibility(rightBootBone, false);
        setBoneVisibility(leftBootBone, false);
        PlayerModel<Player> playerModel = getParentModel();

        switch (category) {
            case Head -> {
                GeoBone bone = (GeoBone) this.modelProvider.getBone(headBone);
                if(!gui)
                    GeoUtils.copyRotations(getParentModel().head, bone);
                else
                    RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
                setBoneVisibility(headBone, true);
                bone.setPosition(playerModel.head.x, -playerModel.head.y, playerModel.head.z);
            }
            case Body -> {
                GeoBone bodyGeoBone = (GeoBone) this.modelProvider.getBone(bodyBone);
                GeoBone rightArmGeoBone = (GeoBone) this.modelProvider.getBone(rightArmBone);
                GeoBone leftArmGeoBone = (GeoBone) this.modelProvider.getBone(leftArmBone);
                if(!gui) {
                    GeoUtils.copyRotations(getParentModel().body, bodyGeoBone);
                    GeoUtils.copyRotations(getParentModel().rightArm, rightArmGeoBone);
                    GeoUtils.copyRotations(getParentModel().leftArm, leftArmGeoBone);
                }
                else {
                    RenderUtils.translateAwayFromPivotPoint(poseStack, bodyGeoBone);
                }
                setBoneVisibility(bodyBone, true);
                setBoneVisibility(rightArmBone, true);
                setBoneVisibility(leftArmBone, true);
                bodyGeoBone.setPosition(playerModel.body.x, -playerModel.body.y, playerModel.body.z);
                rightArmGeoBone.setPosition(playerModel.rightArm.x + 5, 2 - playerModel.rightArm.y, playerModel.rightArm.z);
                leftArmGeoBone.setPosition(playerModel.leftArm.x - 5, 2 - playerModel.leftArm.y, playerModel.leftArm.z);
            }
            case Legs -> {
                GeoBone rightLegGeoBone = (GeoBone) this.modelProvider.getBone(rightLegBone);
                GeoBone leftLegGeoBone = (GeoBone) this.modelProvider.getBone(leftLegBone);
                if(!gui) {
                    GeoUtils.copyRotations(getParentModel().rightLeg, rightLegGeoBone);
                    GeoUtils.copyRotations(getParentModel().leftLeg, leftLegGeoBone);
                }
                else {
                    RenderUtils.translateAwayFromPivotPoint(poseStack, rightLegGeoBone);
                    RenderUtils.translateAwayFromPivotPoint(poseStack, leftLegGeoBone);
                }
                setBoneVisibility(rightLegBone, true);
                setBoneVisibility(leftLegBone, true);
                rightLegGeoBone.setPosition(playerModel.rightLeg.x + 2, 12 - playerModel.rightLeg.y, playerModel.rightLeg.z);
                leftLegGeoBone.setPosition(playerModel.leftLeg.x - 2, 12 - playerModel.leftLeg.y, playerModel.leftLeg.z);
            }
            case Feet -> {
                GeoBone rightBootGeoBone = (GeoBone) this.modelProvider.getBone(rightBootBone);
                GeoBone leftBootGeoBone = (GeoBone) this.modelProvider.getBone(leftBootBone);
                if(!gui) {
                    GeoUtils.copyRotations(getParentModel().rightLeg, rightBootGeoBone);
                    GeoUtils.copyRotations(getParentModel().leftLeg, leftBootGeoBone);
                }
                else {
                    RenderUtils.translateAwayFromPivotPoint(poseStack, rightBootGeoBone);
                    RenderUtils.translateAwayFromPivotPoint(poseStack, leftBootGeoBone);
                }
                setBoneVisibility(rightBootBone, true);
                setBoneVisibility(leftBootBone, true);
                rightBootGeoBone.setPosition(playerModel.rightLeg.x + 2, 12 - playerModel.rightLeg.y, playerModel.rightLeg.z);
                leftBootGeoBone.setPosition(playerModel.leftLeg.x - 2, 12 - playerModel.leftLeg.y, playerModel.leftLeg.z);
            }
        }
    }

    private void setBoneVisibility(String bone, boolean isVisible) {
        this.modelProvider.getBone(bone).setHidden(!isVisible);
    }

    @Override
    public void setCurrentRTB(MultiBufferSource bufferSource) {
        this.rtb = bufferSource;
    }

    @Override
    public MultiBufferSource getCurrentRTB() {
        return this.rtb;
    }

    @Override
    public AnimatedGeoModel<Garment> getGeoModelProvider() {
        return this.modelProvider;
    }

    @Override
    public ResourceLocation getTextureLocation_geckolib(Garment garment) {
        return this.modelProvider.getTextureLocation(garment);
    }
}
