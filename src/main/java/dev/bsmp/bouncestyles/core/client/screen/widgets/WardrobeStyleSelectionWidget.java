package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.screen.widgets.button.StyleSelectionButton;
import dev.bsmp.bouncestyles.core.data.Style;
import dev.bsmp.bouncestyles.core.data.Category;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.Profiler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WardrobeStyleSelectionWidget extends WardrobeScrollWidget implements WardrobeWidget {
    Category category;
    @Nullable SelectionPopup popup = null;

    public WardrobeStyleSelectionWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal("Wardrobe Selection"));
        this.left = x + 5;
        this.top = y + 2;

        updateButtons(Category.Head, new ArrayList<>());
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        if (this.popup != null) {
            this.popup.renderWidget(context, mouseX, mouseY, partialTick);
        }
        else
            super.renderWidget(context, mouseX, mouseY, partialTick);
    }

    @Override
    public void onSelectionClicked(StyleSelectionButton button, int mouseButton) {
        Style style = button.getStyle();
        if (style.getTextureVariants().isPresent() && mouseButton != 1) {
            int popupWidth = this.width - 10;
            int popupHeight = this.height - 10;
            int popupX = getX() + (this.width / 2) - (popupWidth / 2);
            int popupY = getY() + (this.height / 2) - (popupHeight / 2);
            this.popup = new SelectionPopup(this, this.category, style, popupX, popupY, popupWidth, popupHeight);
        }
        else {
            if (this.selectedButton == button) {
                new EquipStyleServerbound(this.category).sendToServer();
                this.selectedButton = null;
            } else if (mouseButton != 1) {
                new EquipStyleServerbound(this.category, style.getStyleId()).sendToServer();
                this.selectedButton = button;
            }
        }
    }

    public Category getCategory() {
        return category;
    }

    public void updateButtons(Category category, List<Style> styles) {
        this.category = category;
        this.scroll = 0;
        this.buttons.clear();
        this.styles = new ArrayList<>(styles);

        this.updateButtons = true;
    }

    public void refresh() {
        this.updateButtons = true;
    }

    @Override
    protected void updateButtons() {
        this.buttons.clear();
        StyleData styleData = StyleData.getOrCreateStyleData(Minecraft.getInstance().player);

        for (Style style : this.styles) {
            StyleSelectionButton button = new StyleSelectionButton(this, 0, 0, buttonSize, buttonSize, category, style);

            var equippedStyle = styleData.getStyleForSlot(category);
            if (equippedStyle.getStyleId().orElse(null) == style.getStyleId()) {
                if (style.getTextureVariants().isPresent())
                    button.setTextureId(equippedStyle.getVariant());
                this.selectedButton = button;
            }

            this.buttons.add(button);
        }

        this.updateVisibleButtons();
        this.updateButtons = false;
    }

    //? if <= 1.20.1 {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.popup != null)
            return this.popup.mouseScrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    *///?} else if >= 1.21.1 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.popup != null)
            return this.popup.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    //?}

    //? if >= 1.21.11 {
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isMouseOver(event.x(), event.y())) {
            if (this.popup == null || !this.popup.mouseClicked(event, doubleClick)) {
                return super.mouseClicked(event, doubleClick);
            }
        }
        this.popup = null;
        return false;
    }
    //? } else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.clicked(mouseX, mouseY)) {
            if (this.popup != null)
                return this.popup.mouseClicked(mouseX, mouseY, mouseButton);
            else
                return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        this.popup = null;
        return false;
    }
    *///? }

    //? if >= 1.21.11 {
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == InputConstants.KEY_ESCAPE && this.popup != null) {
            this.popup = null;
            return true;
        }
        return false;
    }
    //? } else {
    /*@Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.popup != null) {
            this.popup = null;
            return true;
        }
        return false;
    }
    *///? }

    public static class SelectionPopup extends WardrobeScrollWidget {
        private final WardrobeStyleSelectionWidget parent;
        private final Category category;
        private final Style style;

        public SelectionPopup(WardrobeStyleSelectionWidget parent, Category category, Style style, int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty());
            this.parent = parent;
            this.category = category;
            this.style = style;
            this.updateButtons = true;
            this.left = x + 5;
            this.top = y + 5;
        }

        @Override
        protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
            context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFF0092c5);
            context.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, 0xFF004c69);
            context.fill(getX() + 2, getY() + 2, getX() + getWidth() - 2, getY() + getHeight() - 2, 0xFF212121);
            super.renderWidget(context, mouseX, mouseY, partialTick);
        }

        @Override
        public void onSelectionClicked(StyleSelectionButton button, int mouseButton) {
            if (this.selectedButton == button) {
                new EquipStyleServerbound(this.category).sendToServer();
                this.selectedButton = null;
            } else {
                new EquipStyleServerbound(this.category, this.style.getStyleId(), button.getTextureId()).sendToServer();
                this.selectedButton = button;
            }

            this.parent.updateButtons = true;
            this.parent.popup = null;
        }

        @Override
        protected void updateButtons() {
            List<Identifier> textureVariants = style.getTextureVariants().get();
            StyleData styleData = StyleData.getOrCreateStyleData(Minecraft.getInstance().player);

            for (int textureId = -1; textureId < textureVariants.size(); textureId++) {
                StyleSelectionButton button = new StyleSelectionButton(this, 0, 0, buttonSize, buttonSize, category, style);
                button.setTextureId(textureId);

                var equippedStyle = styleData.getStyleForSlot(category);
                if (equippedStyle.getStyleId().orElse(null) == style.getStyleId() && equippedStyle.getVariant() == textureId)
                    this.selectedButton = button;

                this.buttons.add(button);
            }

            this.updateVisibleButtons();
            this.updateButtons = false;
        }

        //? if >= 1.21.11 {
        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (!this.isMouseOver(event.x(), event.y())) {
                this.parent.popup = null;
                return true;
            }
            super.mouseClicked(event, doubleClick);
            return true;
        }
        //? } else {
        /*@Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            if (!this.isMouseOver(mouseX, mouseY)) {
                this.parent.popup = null;
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        *///? }
    }
}
