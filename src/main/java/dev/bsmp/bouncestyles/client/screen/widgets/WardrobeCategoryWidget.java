package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class WardrobeCategoryWidget extends ClickableWidget {
    final WardrobeScreen parentScreen;
    List<CategoryButton> buttonList = new ArrayList<>();
    CategoryButton selectedButton = null;

    public WardrobeCategoryWidget(WardrobeScreen parentScreen, int x, int y, int width, int height) {
        super(x, y, width, height, new LiteralText("Wardrobe Categories"));
        this.parentScreen = parentScreen;
        int i = 0;
        double guiScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
        for(StyleLoader.Category category : StyleLoader.Category.values()) {
            CategoryButton button = new CategoryButton(this, category, x + (int)(10 / guiScale) + (i * height) + (i * (int) (10 / guiScale)), y, height, height);
            this.buttonList.add(button);
            if(category == StyleLoader.Category.Head)
                this.selectedButton = button;
            i++;
        }
    }

    @Override
    public void renderButton(MatrixStack poseStack, int mouseX, int mouseY, float partialTick) {
        for (CategoryButton button : this.buttonList) {
            button.render(poseStack, mouseX, mouseY, partialTick);
        }
        if(hovered) {
            for (CategoryButton button : this.buttonList) {
                if(button.isHovered())
                    button.renderTooltip(poseStack, mouseX, mouseY);
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

    public static class CategoryButton extends ButtonWidget {
        private static final Identifier TEX_CATEGORY_BG = new Identifier(BounceStyles.modId, "textures/icon/category_bg.png");
        WardrobeCategoryWidget parentWidget;
        StyleLoader.Category category;

        public CategoryButton(WardrobeCategoryWidget parentWidget, StyleLoader.Category category, int x, int y, int width, int height) {
            super(x, y, width, height, new LiteralText(category.name()), null);
            this.parentWidget = parentWidget;
            this.category = category;
        }

        @Override
        public void renderButton(MatrixStack poseStack, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEX_CATEGORY_BG);
            RenderSystem.enableDepthTest();
            drawTexture(poseStack, this.x, this.y, this.width, this.height, 0,
                    this.parentWidget.selectedButton == this ? 48 : this.isHovered() ? 24 : 0,
                    24, 24, 24, 72);

            RenderSystem.setShaderTexture(0, this.category.categoryIcon);
            if(category == StyleLoader.Category.Body)
                drawTexture(poseStack, this.x + 3, this.y + 3, this.width - 6, this.height - 6, 0, 0, 16, 16, 16, 16);
            else
                drawTexture(poseStack, this.x + 2, this.y + 2, this.width - 4, this.height - 4, 0, 0, 16, 16, 16, 16);
        }

        @Override
        public void onPress() {
            if(this.parentWidget.selectedButton != this) {
                this.parentWidget.selectedButton = this;
                this.parentWidget.parentScreen.setSelectedCategory(this.category);
            }
        }

        @Override
        public void renderTooltip(MatrixStack poseStack, int mouseX, int mouseY) {
            WardrobeScreen.drawTooltip(getMessage(), mouseX, mouseY, MinecraftClient.getInstance().textRenderer, poseStack, 0);
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder narrationElementOutput) {}
}
