package dev.bsmp.bouncestyles.core.client.screen.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class WardrobeIconButton extends Button {
    ResourceLocation texture;
    ResourceLocation textureHovered;

    public WardrobeIconButton(int x, int y, int width, int height, ResourceLocation texture, ResourceLocation textureHovered, OnPress onPress) {
        this(x, y, width, height, texture, textureHovered, Component.empty(), onPress);
    }

    public WardrobeIconButton(int x, int y, int width, int height, ResourceLocation texture, ResourceLocation textureHovered, Component message, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, Supplier::get);
        this.texture = texture;
        this.textureHovered = textureHovered;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //? if <= 1.20.1 {
        /*guiGraphics.blit(this.isHovered() ? this.textureHovered : this.texture, this.getX(), this.getY(), 0, 0, 0, this.width, this.height, this.width, this.height);
        *///?} else if >= 1.21.1 {
        guiGraphics.blit(this.isHovered() ? this.textureHovered : this.texture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        //?}
    }
}
