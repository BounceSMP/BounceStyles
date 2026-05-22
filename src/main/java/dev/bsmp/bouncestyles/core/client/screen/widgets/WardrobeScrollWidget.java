package dev.bsmp.bouncestyles.core.client.screen.widgets;

import dev.bsmp.bouncestyles.core.client.screen.widgets.button.StyleSelectionButton;
import dev.bsmp.bouncestyles.core.data.Style;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

//? if >= 1.21.11 {
//? } else {
//import com.mojang.blaze3d.platform.GlStateManager;
//? }

public abstract class WardrobeScrollWidget extends AbstractWidget {
    protected static final int buttonSize = 50;
    protected static final int  margin = 3;

    protected int scroll = 0;
    protected int rows;
    protected int columns;
    protected int left;
    protected int top;
    float previewRotation = -30f;
    protected @Nullable StyleSelectionButton selectedButton;

    protected boolean updateButtons = false;
    protected boolean updateVisible = false;
    protected List<Style> styles = new ArrayList<>();
    protected List<StyleSelectionButton> buttons = new ArrayList<>();
    protected List<StyleSelectionButton> visibleButtons = new ArrayList<>();

    public WardrobeScrollWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.rows = this.height / (buttonSize + margin);
        this.columns = (this.width - 5) / (buttonSize + margin);
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        this.previewRotation += 0.05f * partialTick;
        if (this.updateButtons)
            this.updateButtons();
        if (this.updateVisible)
            this.updateVisibleButtons();

        StyleSelectionButton tooltipButton = null;

        for (StyleSelectionButton button : this.visibleButtons) {
            //? if >= 1.21.11 {
            button.render(context, mouseX, mouseY, partialTick);
            //? } else {
//            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
//            button.renderWidget(context, bufferSource, mouseX, mouseY, partialTick);
            //? }
            if (button.isHovered())
                tooltipButton = button;
        }

        //? if < 1.21.11 {
        /*if (tooltipButton != null) {
            tooltipButton.renderTooltip(context, mouseX, mouseY);
        }
        *///? }

        if (this.buttons.size() > this.rows * this.columns) {
            int barWidth = 6;
            int barLeft = getX() + width - barWidth - 3;
            int barTop = getY() + 10;
            int barHeight = height - 20;
            int barBottom = barTop + barHeight;

            int maxScroll = getTotalRows() - rows;
            int scrollHeight = barHeight / maxScroll;
            int scrollTop = this.scroll * (barBottom - barTop - scrollHeight) / maxScroll + barTop;

            context.fill(barLeft - 1, barTop - 1, barLeft + barWidth + 1, barTop + barHeight + 1, 0xFF00a8a8);
            context.fill(barLeft, barTop, barLeft + barWidth, barTop + barHeight, 0xFF212121);
            context.fill(barLeft + 1, scrollTop + 1, barLeft + barWidth - 1, scrollTop + scrollHeight - 1, 0xFF0092c5);
        }
    }

    public abstract void onSelectionClicked(StyleSelectionButton button, int mouseButton);
    protected abstract void updateButtons();

    protected void updateVisibleButtons() {
        this.visibleButtons.clear();

        int index = 0;
        int startingIndex = this.scroll * this.columns;
        int endIndex = startingIndex + (this.rows * this.columns);

        for (int i = Math.max(this.scroll * this.columns, 0); i < endIndex; i++) {
            if (i < this.buttons.size()) {
                StyleSelectionButton button = this.buttons.get(i);
                setButtonPosition(button, index);
                this.visibleButtons.add(button);
            }
            index++;
        }

        this.updateVisible = false;
    }

    private void setButtonPosition(StyleSelectionButton button, int index) {
        int col = index % columns;
        int row = index / columns;
        button.setPosition(this.left + (col * (buttonSize + margin)), this.top + (row * (buttonSize + margin)));
    }

    //? if <= 1.20.1 {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.scroll = Math.min(Math.max(this.scroll - (int) delta, 0), getTotalRows() - this.rows);
        this.updateVisible = true;
        return true;
    }
    *///?} else if >= 1.21.1 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scroll = Math.min(Math.max(this.scroll - (int) scrollY, 0), getTotalRows() - this.rows);
        this.updateVisible = true;
        return true;
    }
    //?}

    //? if >= 1.21.11 {
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        for (StyleSelectionButton button : this.visibleButtons)
            if (button.mouseClicked(event, doubleClick))
                return true;
        return false;
    }
    //? } else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (StyleSelectionButton button : this.visibleButtons)
            if(button.mouseClicked(mouseX, mouseY, mouseButton))
                return true;
        return false;
    }
    *///? }

    private int getTotalRows() {
        return (this.buttons.size() / this.columns) + 1;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}
}
