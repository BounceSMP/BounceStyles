//? if >= 1.21.11 {
package dev.bsmp.bouncestyles.core.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.api.data.EquippedStyle;
import dev.bsmp.bouncestyles.api.animation.AnimState;
import dev.bsmp.bouncestyles.core.data.animation.AnimationHandler;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.api.data.StyleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Pose;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.GeckoLibClientServices;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.List;
import java.util.Objects;

import static dev.bsmp.bouncestyles.core.client.renderer.StyleDataTickets.*;

@SuppressWarnings("UnstableApiUsage")
public class StyleLayerRenderer extends RenderLayer<AvatarRenderState, PlayerModel> implements GeoRenderer<Style, StyleData, GeoRenderState.Impl> {
    public static final StyleGeoModel geoModel = new StyleGeoModel();

    public StyleLayerRenderer(RenderLayerParent<AvatarRenderState, PlayerModel> context) {
        super(context);
    }

    //? if >= 1.21.9 {
    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, AvatarRenderState avatarState, float yRot, float xRot) {
        var cameraState = Minecraft.getInstance().gameRenderer.getLevelRenderState().cameraRenderState;

        var styleData = ((GeoRenderState) avatarState).getGeckolibData(TICKET_STYLE_DATA);
        if (styleData != null) {
            styleData.getAllNonEmpty().forEach((category, equippedStyle) -> {
                GeoRenderer.super.performRenderPass(setupRenderState(styleData, equippedStyle, category, avatarState, packedLight), poseStack, submitNodeCollector, cameraState);
            });
        }
    }
    //? } else {
    //? }

    private GeoRenderState.Impl setupRenderState(StyleData styleData, EquippedStyle equippedStyle, Category category, AvatarRenderState playerState, int packedLight) {
        var renderState = createRenderState(equippedStyle.getStyle().get(), styleData);
        renderState.addGeckolibData(DataTickets.PACKED_LIGHT, packedLight);

        setupAnimationState(renderState, playerState);
        fillRenderState(equippedStyle.getStyle().get(), styleData, renderState, ((GeoRenderState) playerState).getGeckolibData(DataTickets.PARTIAL_TICK));

        renderState.addGeckolibData(TICKET_STYLE, equippedStyle);
        renderState.addGeckolibData(TICKET_CATEGORY, category);

        return renderState;
    }

    public void setupAnimationState(GeoRenderState geoRenderState, AvatarRenderState avatarState) {
        if (avatarState.pose == Pose.SLEEPING)
            geoRenderState.addGeckolibData(AnimationHandler.TICKET_ANIM_STATE, AnimState.SLEEPING);

        else if (avatarState.isInWater && avatarState.isVisuallySwimming)
            geoRenderState.addGeckolibData(AnimationHandler.TICKET_ANIM_STATE, AnimState.SWIMMING);

        else if (avatarState.isFallFlying)
            geoRenderState.addGeckolibData(AnimationHandler.TICKET_ANIM_STATE, AnimState.FLYING);

        else if (!((GeoRenderState) avatarState).getGeckolibData(AnimationHandler.TICKET_ON_GROUND))
            geoRenderState.addGeckolibData(AnimationHandler.TICKET_ANIM_STATE, AnimState.IN_AIR);

        else if (avatarState.isCrouching)
            geoRenderState.addGeckolibData(AnimationHandler.TICKET_ANIM_STATE, AnimState.SNEAKING);

        else if (((GeoRenderState) avatarState).getGeckolibData(DataTickets.IS_MOVING)) {
            if (((GeoRenderState) avatarState).getGeckolibData(AnimationHandler.TICKET_SPRINTING))
                geoRenderState.addGeckolibData(AnimationHandler.TICKET_ANIM_STATE, AnimState.SPRINTING);
            else
                geoRenderState.addGeckolibData(AnimationHandler.TICKET_ANIM_STATE, AnimState.WALKING);
        }
        else
            geoRenderState.addGeckolibData(AnimationHandler.TICKET_ANIM_STATE, AnimState.IDLE);
    }

    //? if >= 1.21.11 {
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
    public boolean firePreRenderEvent(@NonNull RenderPassInfo<GeoRenderState.Impl> renderPassInfo, @NonNull SubmitNodeCollector renderTasks) {
        return GeckoLibClientServices.EVENTS.fireObjectPreRender(renderPassInfo, renderTasks);
    }
    //? }

    @Override
    public void fireCompileRenderStateEvent(Style animatable, @Nullable StyleData styleData, GeoRenderState.Impl renderState, float partialTick) {}

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
    public @NonNull GeoModel<Style> getGeoModel() {
        return geoModel;
    }

    @Override
    public @Nullable RenderType getRenderType(GeoRenderState.Impl renderState, Identifier texture) {
        //ToDo Allow choosing rendertype per-style?
        //~ if >= 1.21.11 'RenderType' -> 'RenderTypes'
        return RenderTypes.entityCutoutNoCull(texture);
    }

    public GeoRenderState.Impl createRenderState(Style style, @Nullable StyleData styleData) {
        return new GeoRenderState.Impl();
    }
}
//? }