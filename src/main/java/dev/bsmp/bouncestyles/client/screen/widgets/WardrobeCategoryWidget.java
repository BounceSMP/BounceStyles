package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class WardrobeCategoryWidget extends AbstractWidget {
    final WardrobeScreen parentScreen;
    List<CategoryButton> buttonList = new ArrayList<>();
    CategoryButton selectedButton = null;

    public WardrobeCategoryWidget(WardrobeScreen parentScreen, int x, int y, int width, int height) {
        super(x, y, width, height, new TextComponent("Wardrobe Categories"));
        this.parentScreen = parentScreen;
        int i = 0;
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        for(StyleLoader.Category category : StyleLoader.Category.values()) {
            CategoryButton button = new CategoryButton(this, category, x + (int)(10 / guiScale) + (i * height) + (i * (int) (10 / guiScale)), y, height, height);
            this.buttonList.add(button);
            if(category == StyleLoader.Category.Head)
                this.selectedButton = button;
            i++;
        }
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        for (CategoryButton button : this.buttonList) {
            button.render(poseStack, mouseX, mouseY, partialTick);
        }
        if(isHovered) {
            for (CategoryButton button : this.buttonList) {
                if(button.isHoveredOrFocused())
                    button.renderToolTip(poseStack, mouseX, mouseY);
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

    public static class CategoryButton extends Button {
        private static final ResourceLocation TEX_CATEGORY_BG = new ResourceLocation(BounceStyles.modId, "textures/icon/category_bg.png");
        WardrobeCategoryWidget parentWidget;
        StyleLoader.Category category;

        public CategoryButton(WardrobeCategoryWidget parentWidget, StyleLoader.Category category, int x, int y, int width, int height) {
            super(x, y, width, height, new TextComponent(category.name()), null);
            this.parentWidget = parentWidget;
            this.category = category;
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEX_CATEGORY_BG);
            RenderSystem.enableDepthTest();
            blit(poseStack, this.x, this.y, this.width, this.height, 0,
                    this.parentWidget.selectedButton == this ? 48 : this.isHoveredOrFocused() ? 24 : 0,
                    24, 24, 24, 72);

            RenderSystem.setShaderTexture(0, this.category.categoryIcon);
            if(category == StyleLoader.Category.Body)
                blit(poseStack, this.x + 3, this.y + 3, this.width - 6, this.height - 6, 0, 0, 16, 16, 16, 16);
            else
                blit(poseStack, this.x + 2, this.y + 2, this.width - 4, this.height - 4, 0, 0, 16, 16, 16, 16);
        }

        @Override
        public void onPress() {
            if(this.parentWidget.selectedButton != this) {
                this.parentWidget.selectedButton = this;
                this.parentWidget.parentScreen.setSelectedCategory(this.category);
            }
        }

        @Override
        public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
            Font font = Minecraft.getInstance().font;
            int textWidth = font.width(getMessage());
            fill(poseStack, mouseX + 2, mouseY - 12, mouseX + textWidth + 7, mouseY + 2, 0xFF000000);
            drawString(poseStack, Minecraft.getInstance().font, getMessage(), mouseX + 5, mouseY - 9, 0xFFFFFF);
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}
