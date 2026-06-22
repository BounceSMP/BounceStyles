//? if >= 1.21.5 {
package dev.bsmp.bouncestyles.core.client.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class StyleGuiRenderer extends PictureInPictureRenderer<StyleGuiRenderer.StyleGuiRenderState> {
    public StyleGuiRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    protected void renderToTexture(StyleGuiRenderState renderState, PoseStack poseStack) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);

        poseStack.translate(renderState.translation.x(), renderState.translation.y(), renderState.translation.z());
        poseStack.mulPose(renderState.rotation);
        if (renderState.isHovered()) poseStack.scale(1.2f, 1.2f, 1.2f);

        var featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        BounceStylesClient.getStyleRenderer().performRenderPass(
                renderState.renderState,
                poseStack,
                featureRenderDispatcher.getSubmitNodeStorage(),
                new CameraRenderState()
        );
        featureRenderDispatcher.renderAllFeatures();
    }

    @Override
    protected String getTextureLabel() {
        return "style_preview";
    }

    @Override
    public Class<StyleGuiRenderState> getRenderStateClass() {
        return StyleGuiRenderState.class;
    }

    public record StyleGuiRenderState(AvatarRenderState renderState, Vector3f translation, Quaternionf rotation, int x0, int y0, int x1, int y1, float scale, boolean isHovered, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {
        public StyleGuiRenderState(AvatarRenderState renderState, Vector3f translation, Quaternionf rotation, int x0, int y0, int x1, int y1, float scale, boolean isHovered, @Nullable ScreenRectangle scissorArea) {
            this(renderState, translation, rotation, x0, y0, x1, y1, scale, isHovered, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
        }
    }
}
//? }