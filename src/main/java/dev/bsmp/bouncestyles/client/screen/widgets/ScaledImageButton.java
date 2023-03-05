package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ScaledImageButton extends TexturedButtonWidget {
    Identifier resourceLocation;
    int uWidth;
    int vHeight;
    int xTexStart;
    int yTexStart;

    public ScaledImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int uWidth, int vHeight, Identifier resourceLocation, PressAction onPress) {
        super(x, y, width, height, xTexStart, yTexStart, resourceLocation, onPress);
        this.resourceLocation = resourceLocation;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
    }

    @Override
    public void renderButton(MatrixStack poseStack, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        int i = this.yTexStart;
        if (this.isHovered()) {
            i += this.vHeight;
        }
        RenderSystem.enableDepthTest();
        TexturedButtonWidget.drawTexture(poseStack, this.x, this.y, this.width, this.height, this.xTexStart, i, this.uWidth, this.vHeight, 256, 256);
        if (this.hovered) {
            this.renderTooltip(poseStack, mouseX, mouseY);
        }
    }
}
