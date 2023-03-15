package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ScaledImageButton extends TexturedButtonWidget {
    Identifier resourceLocation;
    int uWidth;
    int vHeight;
    int xTexStart;
    int yTexStart;

    public ScaledImageButton(Text tooltip, int x, int y, int width, int height, int xTexStart, int yTexStart, int uWidth, int vHeight, Identifier resourceLocation, PressAction onPress) {
        super(x, y, width, height, xTexStart, yTexStart, vHeight, resourceLocation, onPress);
        this.resourceLocation = resourceLocation;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        if(tooltip != null)
            setMessage(tooltip);
    }

    @Override
    public void renderButton(MatrixStack poseStack, int mouseX, int mouseY, float partialTick) {
        int i = this.yTexStart;
        if (this.isHovered()) {
            i += this.vHeight;
        }
        MinecraftClient.getInstance().getTextureManager().bindTexture(this.resourceLocation);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.enableDepthTest();
        drawTexture(poseStack, this.x, this.y, this.width, this.height, this.xTexStart, i, this.uWidth, this.vHeight, 256, 256);
        if (this.hovered) {
            this.renderToolTip(poseStack, mouseX, mouseY);
        }
    }

    @Override
    public void renderToolTip(MatrixStack poseStack, int mouseX, int mouseY) {
        if(this.getMessage() != null)
            WardrobeScreen.drawTooltip(getMessage(), mouseX, mouseY, MinecraftClient.getInstance().textRenderer, poseStack, MinecraftClient.getInstance().getWindow().getScaledWidth());
    }

}
