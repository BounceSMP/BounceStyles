package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.client.screen.WardrobeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class WardrobeCategoryWidget extends AbstractWidget implements WardrobeWidget {
    final WardrobeScreen parentScreen;
    List<CategoryButton> buttonList = new ArrayList<>();
    CategoryButton selectedButton = null;

    public WardrobeCategoryWidget(WardrobeScreen parentScreen, int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal("Wardrobe Categories"));
        this.parentScreen = parentScreen;
        int i = 0;
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        for(BounceStylesRegistries.Category category : BounceStylesRegistries.Category.values()) {
            CategoryButton button = new CategoryButton(this, category, x + (int)(10 / guiScale) + (i * height) + (i * (int) (10 / guiScale)), y, height, height);
            this.buttonList.add(button);
            if(category == BounceStylesRegistries.Category.Head)
                this.selectedButton = button;
            i++;
        }
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        for (CategoryButton button : this.buttonList) {
            button.render(context, mouseX, mouseY, partialTick);
        }
        if(isHovered) {
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
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    public class CategoryButton extends Button {
        private static final ResourceLocation TEX_CATEGORY_BG = BounceStyles.resourceLocation("textures/icon/category_bg.png");
        WardrobeCategoryWidget parentWidget;
        BounceStylesRegistries.Category category;

        public CategoryButton(WardrobeCategoryWidget parentWidget, BounceStylesRegistries.Category category, int x, int y, int width, int height) {
            super(x, y, width, height, Component.literal(category.name()), null, DEFAULT_NARRATION);
            this.parentWidget = parentWidget;
            this.category = category;
        }

        @Override
        public void renderWidget(GuiGraphics drawContext, int mouseX, int mouseY, float partialTick) {
            RenderSystem.enableDepthTest();
            drawContext.blit(TEX_CATEGORY_BG,  getX(), getY(), this.width, this.height, 0, this.parentWidget.selectedButton == this ? 48 : this.isHovered() ? 24 : 0, 24, 24, 24, 72);
            if(category == BounceStylesRegistries.Category.Body)
                drawContext.blit(this.category.categoryIcon, getX() + 3, getY() + 3, this.width - 6, this.height - 6, 0, 0, 16, 16, 16, 16);
            else
                drawContext.blit(this.category.categoryIcon, getX() + 2, getY() + 2, this.width - 4, this.height - 4, 0, 0, 16, 16, 16, 16);
        }

        @Override
        public void onPress() {
            if(this.parentWidget.selectedButton != this) {
                this.parentWidget.selectedButton = this;
                this.parentWidget.parentScreen.setSelectedCategory(this.category);
            }
        }

        public void renderTooltip(GuiGraphics poseStack, int mouseX, int mouseY) {
            drawTooltip(poseStack, Minecraft.getInstance().font, getMessage(), mouseX, mouseY, 0);
        }
    }
}
