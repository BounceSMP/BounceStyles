package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ScaledImageButton extends ImageButton implements WardrobeWidget {
    ResourceLocation resourceLocation;
    int uWidth;
    int vHeight;

    public ScaledImageButton(Component tooltip, int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, ResourceLocation resourceLocation, OnPress onPress) {
        super(x, y, width, height, u, v, resourceLocation, onPress);
        this.resourceLocation = resourceLocation;
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        setMessage(tooltip);
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableDepthTest();
        context.blit(this.resourceLocation, getX(), getY(), getWidth(), getHeight(), xTexStart, yTexStart, this.uWidth, this.vHeight,256, 256);
        if (this.isHovered)
            this.renderTooltip(context, mouseX, mouseY);
    }

    public void renderTooltip(GuiGraphics poseStack, int mouseX, int mouseY) {
        if(this.getMessage() != null)
            drawTooltip(getMessage(), mouseX, mouseY, Minecraft.getInstance().font, poseStack, Minecraft.getInstance().getWindow().getGuiScaledWidth());
    }
}
