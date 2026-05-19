package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jspecify.annotations.Nullable;

public record GuiStyleRenderState(StyleLayerRenderer.StyleRenderState renderState) implements GuiElementRenderState {
    @Override
    public void buildVertices(VertexConsumer consumer) {
    }

    @Override
    public RenderPipeline pipeline() {
        return null;
    }

    @Override
    public TextureSetup textureSetup() {
        return null;
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        return null;
    }

    @Override
    public @Nullable ScreenRectangle bounds() {
        return null;
    }
}
