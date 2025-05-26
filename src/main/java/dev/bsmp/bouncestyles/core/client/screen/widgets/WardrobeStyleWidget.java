package dev.bsmp.bouncestyles.core.client.screen.widgets;

import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries.Category;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WardrobeStyleWidget extends WardrobeScrollWidget implements WardrobeWidget {
    Category category;
    @Nullable SelectionPopup popup = null;

    public WardrobeStyleWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal("Wardrobe Selection"));
        this.left = x + 5;
        this.top = y + 2;

        updateButtons(Category.Head, new ArrayList<>());
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        if (this.popup != null) {
            context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x99000000);
            this.popup.renderWidget(context, mouseX, mouseY, partialTick);
        }
        else
            super.renderWidget(context, mouseX, mouseY, partialTick);
    }

    @Override
    protected void onSelectionClicked(StyleButton button) {
        Style style = button.getStyle();
        if (style.getTextureVariants().isPresent()) {
            int popupWidth = this.width - 10;
            int popupHeight = this.height - (this.height / 6);
            int popupX = getX() + (this.width / 2) - (popupWidth / 2);
            int popupY = getY() + (this.height / 2) - (popupHeight / 2);
            this.popup = new SelectionPopup(this, this.category, style, popupX, popupY, popupWidth, popupHeight);
        }
        else {
            if (this.selectedStyleButton == button) {
                new EquipStyleServerbound(this.category).sendToServer();
                this.selectedStyleButton = null;
            } else {
                new EquipStyleServerbound(this.category, style.getStyleId()).sendToServer();
                this.selectedStyleButton = button;
            }
        }
    }

    public void updateButtons(Category category, List<Style> styles) {
        this.category = category;
        this.scroll = 0;
        this.buttons.clear();
        this.styles = new ArrayList<>(styles);

        this.updateButtons = true;
    }

    @Override
    protected void updateButtons() {
        StyleData styleData = StyleData.getOrCreateStyleData(Minecraft.getInstance().player);

        for (Style style : styles) {
            WardrobeStyleWidget.StyleButton button = new WardrobeStyleWidget.StyleButton(this, 0, 0, buttonSize, buttonSize, category, style);

            var equippedStyle = styleData.getStyleForSlot(category);
            if (equippedStyle.isPresent() && equippedStyle.get().getFirst() == style) {
                if (style.getTextureVariants().isPresent()) button.setTextureId(equippedStyle.get().getSecond());
                this.selectedStyleButton = button;
            }

            this.buttons.add(button);
        }

        this.updateVisibleButtons();
        this.updateButtons = false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.popup != null)
            return this.popup.mouseScrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isValidClickButton(mouseButton) && this.clicked(mouseX, mouseY)) {
            if (this.popup != null)
                return this.popup.mouseClicked(mouseX, mouseY, mouseButton);
            else
                return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.popup != null) {
            this.popup = null;
            return true;
        }
        return false;
    }

    private static class SelectionPopup extends WardrobeScrollWidget {
        private final WardrobeStyleWidget parent;
        private final Category category;
        private final Style style;

        public SelectionPopup(WardrobeStyleWidget parent, Category category, Style style, int x, int y, int width, int height) {
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
            context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFFFFFFFF);
            super.renderWidget(context, mouseX, mouseY, partialTick);
        }

        @Override
        protected void onSelectionClicked(StyleButton button) {
            if (this.selectedStyleButton == button) {
                new EquipStyleServerbound(this.category).sendToServer();
                this.selectedStyleButton = null;
            } else {
                new EquipStyleServerbound(this.category, this.style.getStyleId(), button.getTextureId()).sendToServer();
                this.selectedStyleButton = button;
            }

            this.parent.updateButtons = true;
            this.parent.popup = null;
        }

        @Override
        protected void updateButtons() {
            List<ResourceLocation> textureVariants = style.getTextureVariants().get();
            StyleData styleData = StyleData.getOrCreateStyleData(Minecraft.getInstance().player);

            for (int textureId = -1; textureId < textureVariants.size(); textureId++) {
                WardrobeStyleWidget.StyleButton button = new WardrobeStyleWidget.StyleButton(this, 0, 0, buttonSize, buttonSize, category, style);
                button.setTextureId(textureId);

                var equippedStyle = styleData.getStyleForSlot(category);
                if (equippedStyle.isPresent() && equippedStyle.get().getFirst() == style && equippedStyle.get().getSecond() == textureId)
                    this.selectedStyleButton = button;

                this.buttons.add(button);
            }

            this.updateVisibleButtons();
            this.updateButtons = false;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            if (!this.isMouseOver(mouseX, mouseY)) {
                this.parent.popup = null;
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
}
