package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ScaledImageButton extends TexturedButtonWidget implements WardrobeWidget {
    Identifier resourceLocation;
    int uWidth;
    int vHeight;

    public ScaledImageButton(Text tooltip, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, Identifier resourceLocation, PressAction onPress) {
        super(x, y, width, height, u, v, resourceLocation, onPress);
        this.resourceLocation = resourceLocation;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        setMessage(tooltip);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableDepthTest();
        context.drawTexture(this.resourceLocation, getX(), getY(), getWidth(), getHeight(), u, v, this.uWidth, this.vHeight,256, 256);
        if (this.hovered)
            this.renderTooltip(context, mouseX, mouseY);
    }

    public void renderTooltip(DrawContext poseStack, int mouseX, int mouseY) {
        if(this.getMessage() != null)
            drawTooltip(getMessage(), mouseX, mouseY, MinecraftClient.getInstance().textRenderer, poseStack, MinecraftClient.getInstance().getWindow().getScaledWidth());
    }
}
