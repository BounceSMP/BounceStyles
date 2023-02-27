package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class ScaledImageButton extends ImageButton {
    ResourceLocation resourceLocation;
    int uWidth;
    int vHeight;
    int xTexStart;
    int yTexStart;

    public ScaledImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int uWidth, int vHeight, ResourceLocation resourceLocation, OnPress onPress) {
        super(x, y, width, height, xTexStart, yTexStart, resourceLocation, onPress);
        this.resourceLocation = resourceLocation;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        int i = this.yTexStart;
        if (this.isHoveredOrFocused()) {
            i += this.vHeight;
        }
        RenderSystem.enableDepthTest();
        ImageButton.blit(poseStack, this.x, this.y, this.width, this.height, this.xTexStart, i, this.uWidth, this.vHeight, 256, 256);
        if (this.isHovered) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
    }
}
