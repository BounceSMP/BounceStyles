//? if >= 1.21.5 {
package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public record GuiStyleRenderState(GeoRenderState renderState) implements GuiElementRenderState {
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
//? }