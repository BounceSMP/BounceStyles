package dev.bsmp.bouncestyles.core.client.screen.widgets;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class IconSelectionButton extends Button implements WardrobeWidget {
    private final List<Pair<Component, Runnable>> items = new ArrayList<>();
    private final boolean staticIcon;
    private int selectedIndex = 0;
    private boolean expanded = false;

    private final ResourceLocation texture;
    private final int textureWidth, textureHeight;

    public IconSelectionButton(int x, int y, int width, int height, ResourceLocation resourceLocation, int textureWidth, int textureHeight, boolean staticIcon, Component message) {
        super(x, y, width, height, message, null, supplier -> Component.empty());

        this.staticIcon = staticIcon;
        this.texture = resourceLocation;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int hoverIndex = (mouseY - this.getY()) / this.height;
        guiGraphics.blit(this.texture, getX(), getY(), this.staticIcon ? 0 : this.selectedIndex * width, (this.isMouseOver(mouseX, mouseY) && hoverIndex == 0) ? height : this.expanded ? 2 * height : 0, width, height, textureWidth, textureHeight);

        if (this.expanded) {
            int startY = getY() + height;
            for (int i = 0; i < this.items.size(); i++) {
                guiGraphics.blit(this.texture, getX(), startY + (i * height), (this.staticIcon ? (i + 1) : i) * width, (this.isMouseOver(mouseX, mouseY) && hoverIndex == i + 1) ? height : 0, width, height, textureWidth, textureHeight);
            }
        }

        //ToDo Change up tooltip rendering to account for overlapping widgets
//        if (isMouseOver(mouseX, mouseY)) {
//            Component tooltip;
//            if (hoverIndex == 0) tooltip = this.getMessage();
//            else tooltip = this.items.get(hoverIndex - 1).left();
//            drawTooltip(guiGraphics, Minecraft.getInstance().font, tooltip, mouseX, mouseY);
//        }
    }

    public void addItem(Component label, Runnable onPress) {
        this.items.add(Pair.of(label, onPress));
    }

    @Override
    public int getHeight() {
        int expandedHeight = this.expanded ? this.items.size() * this.height : 0;
        return this.height + expandedHeight;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible
                && mouseX >= (double)this.getX()
                && mouseY >= (double)this.getY()
                && mouseX < (double)(this.getX() + this.width)
                && mouseY < (double)(this.getY() + this.getHeight());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && this.isMouseOver(mouseX, mouseY) && button == 0) {
            int i = (int) ((mouseY - this.getY()) / this.height);

            if (i > 0) {
                this.setSelected(i - 1);
            }

            this.expanded = !this.expanded;
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        else if (expanded) {
            this.expanded = false;
        }
        this.setFocused(false);
        return false;
    }

    public boolean setSelected(int index) {
        if (this.items.size() < index) return false;
        this.selectedIndex = index;
        this.items.get(this.selectedIndex).right().run();
        return true;
    }
}
