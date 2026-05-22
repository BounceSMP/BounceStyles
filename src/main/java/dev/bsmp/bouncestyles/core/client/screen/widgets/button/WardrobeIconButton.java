package dev.bsmp.bouncestyles.core.client.screen.widgets.button;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.screen.widgets.WardrobeWidget;
import dev.bsmp.bouncestyles.core.data.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.function.Supplier;

public class WardrobeIconButton extends Button implements WardrobeWidget {
    private static final Identifier TEX_BASE = id("btn_base", "");
    private static final Identifier TEX_BASE_HOVER = id("btn_base", "hover");
    private static final Identifier TEX_BASE_SELECTED = id("btn_base", "selected");

    private final Identifier iconTexture;
    private final Identifier iconTextureHover;

    public WardrobeIconButton(int x, int y, String icon, OnPress onPress) {
        this(x, y, icon, Component.empty(), onPress);
    }

    public WardrobeIconButton(int x, int y, String icon, Component message, OnPress onPress) {
        super(x, y, 20, 20, message, onPress, Supplier::get);
        this.iconTexture = id(icon, "");
        this.iconTextureHover = id(icon, "hover");
    }

    private static String getPath(String name) {
        return "textures/gui/sprites/"+name+"/";
    }

    private static Identifier id(String name, String suffix) {
        suffix = suffix.isBlank() ? "" : "_" + suffix;
        return BounceStyles.id(getPath(name)+name+suffix+".png");
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var baseTexture = isFocused() ? TEX_BASE_SELECTED : isHovered() ? TEX_BASE_HOVER : TEX_BASE;
        blit(guiGraphics, baseTexture, getX(), getY(), getWidth(), getHeight());
        blit(guiGraphics, isHoveredOrFocused() ? this.iconTextureHover : this.iconTexture, getX() + 2, getY() + 2, 16, 16);

        if (isHovered())
            guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, List.of(this.getMessage()), mouseX + 4, mouseY + 16);
    }
}
