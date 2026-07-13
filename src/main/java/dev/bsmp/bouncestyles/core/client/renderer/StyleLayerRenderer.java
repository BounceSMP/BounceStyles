//? if >= 1.21.11 {
package dev.bsmp.bouncestyles.core.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.api.animation.AnimState;
import dev.bsmp.bouncestyles.api.data.EquippedStyle;
import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.data.animation.AnimationHandler;
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
import net.minecraft.world.entity.Pose;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static dev.bsmp.bouncestyles.core.client.renderer.StyleDataTickets.*;

@SuppressWarnings("UnstableApiUsage")
public class StyleLayerRenderer extends RenderLayer<AvatarRenderState, PlayerModel> implements GeoRenderer<Style, StyleLayerRenderer.RenderData, GeoRenderState.Impl> {
    public static final StyleGeoModel geoModel = new StyleGeoModel();
    private static final HashMap<Identifier, Boolean> emissiveCache = new HashMap<>();

    private final List<GeoRenderLayer<Style, RenderData, GeoRenderState.Impl>> layers = List.of(
            new AutoGlowingGeoLayer(this)
    );

    public StyleLayerRenderer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, AvatarRenderState avatarState, float yRot, float xRot) {
        var cameraState = Minecraft.getInstance().gameRenderer.getLevelRenderState().cameraRenderState;

        var styleData = ((GeoRenderState) avatarState).getGeckolibData(TICKET_STYLE_DATA);
        if (styleData != null) {
            poseStack.pushPose();

            var animation = setupAnimationState(avatarState);

            styleData.getAllNonEmpty().forEach((category, equippedStyle) -> {
                var geoRenderState = setupRenderState(equippedStyle, category, avatarState, animation, packedLight);
                GeoRenderer.super.performRenderPass(geoRenderState, poseStack, submitNodeCollector, cameraState);
            });
            poseStack.popPose();
        }
    }

    private GeoRenderState.Impl setupRenderState(EquippedStyle equippedStyle, Category category, AvatarRenderState playerState, AnimState animState, int packedLight) {
        var renderData = new StyleLayerRenderer.RenderData(playerState.id, playerState);
        var renderState = createRenderState(equippedStyle.getStyle().get(), renderData);

        renderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);
        renderState.addGeckolibData(AnimationHandler.TICKET_ANIM_STATE, animState);
        renderState.addGeckolibData(TICKET_EQUIPPED, equippedStyle);
        renderState.addGeckolibData(TICKET_CATEGORY, category);

        fillRenderState(equippedStyle.getStyle().get(), renderData, renderState, ((GeoRenderState) playerState).getPartialTick());

        return renderState;
    }

    public AnimState setupAnimationState(AvatarRenderState avatarState) {
        if (avatarState.pose == Pose.SLEEPING)
            return AnimState.SLEEPING;

        else if (avatarState.isInWater && avatarState.isVisuallySwimming)
            return AnimState.SWIMMING;

        else if (avatarState.isFallFlying)
            return AnimState.FLYING;

        else if (!((GeoRenderState) avatarState).getGeckolibData(AnimationHandler.TICKET_ON_GROUND))
            return AnimState.IN_AIR;

        else if (avatarState.isCrouching)
            return AnimState.SNEAKING;

        else if (((GeoRenderState) avatarState).getGeckolibData(DataTickets.IS_MOVING)) {
            if (((GeoRenderState) avatarState).getGeckolibData(AnimationHandler.TICKET_SPRINTING))
                return AnimState.SPRINTING;
            else
                return AnimState.WALKING;
        }
        else
            return AnimState.IDLE;
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
                    renderPassInfo.model().getBone(getBoneNameForSegment(segment)).ifPresent(bone ->
                            bone.positionAndRender(renderPassInfo, vertexConsumer, packedLight, packedOverlay, renderColor)
                    );
                }
            });
            poseStack.popPose();
        });
    }

    @Override
    public void applyRenderLayers(RenderPassInfo<GeoRenderState.Impl> renderPassInfo, SubmitNodeCollector renderTasks) {
        for (GeoRenderLayer renderLayer : getRenderLayers()) {
            if (renderLayer instanceof AutoGlowingGeoLayer) {
                var emissiveTexture = RenderUtil.getEmissiveResource(this.getTextureLocation(renderPassInfo.renderState()));

                if (emissiveCache.computeIfAbsent(emissiveTexture, id -> Minecraft.getInstance().getResourceManager().getResource(id).isPresent()))
                    renderLayer.submitRenderTask(renderPassInfo, renderTasks);
                else
                    continue;
            }
            renderLayer.submitRenderTask(renderPassInfo, renderTasks);
        }
    }

    @Override
    public void adjustRenderPose(@NonNull RenderPassInfo<GeoRenderState.Impl> renderPassInfo) {
        renderPassInfo.poseStack().translate(0, 24 / 16f, 0);
        renderPassInfo.poseStack().scale(-1, -1, 1);
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<GeoRenderState.Impl> renderPassInfo, BoneSnapshots snapshots) {
        var renderState = (GeoRenderState) renderPassInfo.renderState();

        var equipped = renderState.getGeckolibData(TICKET_EQUIPPED);
        var category = renderState.getGeckolibData(TICKET_CATEGORY);
        if (equipped.getStyle().isPresent()) {
            this.getParentModel().setupAnim(renderState.getGeckolibData(TICKET_RENDER_DATA).playerState());
            adjustBones(category, snapshots);
        }
    }

    private void adjustBones(Category category, BoneSnapshots snapshots) {
        getSegmentsForCategory(category).forEach(segment ->
                snapshots.get(getBoneNameForSegment(segment)).ifPresent(boneSnapshot -> {
                    final ModelPart modelPart = segment.modelPartGetter.apply(this.getParentModel());
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
    public void fireCompileRenderStateEvent(Style animatable, StyleLayerRenderer.RenderData relatedObject, GeoRenderState.Impl renderState, float partialTick) {}

    @Override
    public void fireCompileRenderLayersEvent() {}

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

    @Override
    public List<GeoRenderLayer<Style, RenderData, GeoRenderState.Impl>> getRenderLayers() {
        return this.layers;
    }

    @Override
    public long getInstanceId(Style animatable, StyleLayerRenderer.RenderData renderState) {
        return renderState.id;
    }

    @Override
    public @NonNull GeoModel<Style> getGeoModel() {
        return geoModel;
    }

    @Override
    public @Nullable RenderType getRenderType(GeoRenderState.Impl renderState, Identifier texture) {
        //ToDo Allow choosing rendertype per-style?
        //~ if >= 1.21.11 'RenderType' -> 'RenderTypes'
        return RenderTypes.entityCutout(texture);
    }

    @Override
    public GeoRenderState.Impl createRenderState(Style style, StyleLayerRenderer.RenderData renderData) {
        var renderState =  new GeoRenderState.Impl();
        renderState.addGeckolibData(TICKET_RENDER_DATA, renderData);
        return renderState;
    }

    public record RenderData(int id, AvatarRenderState playerState) {}
}
//? }