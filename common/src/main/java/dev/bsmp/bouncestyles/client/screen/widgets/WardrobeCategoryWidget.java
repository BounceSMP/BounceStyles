package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleRegistry;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class WardrobeCategoryWidget extends ClickableWidget implements WardrobeWidget {
    final WardrobeScreen parentScreen;
    List<CategoryButton> buttonList = new ArrayList<>();
    CategoryButton selectedButton = null;

    public WardrobeCategoryWidget(WardrobeScreen parentScreen, int x, int y, int width, int height) {
        super(x, y, width, height, Text.literal("Wardrobe Categories"));
        this.parentScreen = parentScreen;
        int i = 0;
        double guiScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
        for(StyleRegistry.Category category : StyleRegistry.Category.values()) {
            CategoryButton button = new CategoryButton(this, category, x + (int)(10 / guiScale) + (i * height) + (i * (int) (10 / guiScale)), y, height, height);
            this.buttonList.add(button);
            if(category == StyleRegistry.Category.Head)
                this.selectedButton = button;
            i++;
        }
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float partialTick) {
        for (CategoryButton button : this.buttonList) {
            button.render(context, mouseX, mouseY, partialTick);
        }
        if(hovered) {
            for (CategoryButton button : this.buttonList) {
                if(button.isHovered())
                    button.renderTooltip(context, mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.isValidClickButton(mouseButton) && this.clicked(mouseX, mouseY)) {
            for (CategoryButton button : this.buttonList) {
                if (button.mouseClicked(mouseX, mouseY, mouseButton))
                    return true;
            }
        }
        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    public class CategoryButton extends ButtonWidget {
        private static final Identifier TEX_CATEGORY_BG = new Identifier(BounceStyles.modId, "textures/icon/category_bg.png");
        WardrobeCategoryWidget parentWidget;
        StyleRegistry.Category category;

        public CategoryButton(WardrobeCategoryWidget parentWidget, StyleRegistry.Category category, int x, int y, int width, int height) {
            super(x, y, width, height, Text.literal(category.name()), null, DEFAULT_NARRATION_SUPPLIER);
            this.parentWidget = parentWidget;
            this.category = category;
        }

        @Override
        public void renderButton(DrawContext drawContext, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableDepthTest();
            drawContext.drawTexture(TEX_CATEGORY_BG,  getX(), getY(), this.width, this.height, 0, this.parentWidget.selectedButton == this ? 48 : this.isHovered() ? 24 : 0, 24, 24, 24, 72);
            if(category == StyleRegistry.Category.Body)
                drawContext.drawTexture(this.category.categoryIcon, getX() + 3, getY() + 3, this.width - 6, this.height - 6, 0, 0, 16, 16, 16, 16);
            else
                drawContext.drawTexture(this.category.categoryIcon, getX() + 2, getY() + 2, this.width - 4, this.height - 4, 0, 0, 16, 16, 16, 16);
        }

        @Override
        public void onPress() {
            if(this.parentWidget.selectedButton != this) {
                this.parentWidget.selectedButton = this;
                this.parentWidget.parentScreen.setSelectedCategory(this.category);
            }
        }

        public void renderTooltip(DrawContext poseStack, int mouseX, int mouseY) {
            drawTooltip(getMessage(), mouseX, mouseY, MinecraftClient.getInstance().textRenderer, poseStack, 0);
        }
    }
}
