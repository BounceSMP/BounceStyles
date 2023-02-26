package dev.bsmp.bouncestyles.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.screen.widgets.WardrobeCategoryWidget;
import dev.bsmp.bouncestyles.client.screen.widgets.WardrobeStyleWidget;
import dev.bsmp.bouncestyles.client.screen.widgets.WardrobePreviewWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class WardrobeScreen extends Screen {

    WardrobeCategoryWidget categoryWidget;
    WardrobeStyleWidget styleWidget;
    WardrobePreviewWidget previewWidget;

    List<ResourceLocation> unlockedStyles;
    StyleLoader.Category selectedCategory;

    public WardrobeScreen(List<ResourceLocation> unlocks) {
        super(new TextComponent("Wardrobe Screen"));
        this.unlockedStyles = unlocks;
    }

    @Override
    protected void init() {
        super.init();
        int previewRight = width / 3;
        int topBarHeight = height / 10;
        this.previewWidget = addRenderableWidget(new WardrobePreviewWidget(0, 0, previewRight, height, minecraft.player));
        this.categoryWidget = addRenderableWidget(new WardrobeCategoryWidget(this, previewRight, 1, width - previewRight, topBarHeight));
        this.styleWidget = addRenderableWidget(new WardrobeStyleWidget(previewRight, topBarHeight, width - previewRight, height - topBarHeight));
        this.setSelectedCategory(StyleLoader.Category.Head);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(PoseStack poseStack) {
        super.renderBackground(poseStack);
        int previewRight = width / 3;
        int barHeight = height / 10;

        fill(poseStack, 0, 0, width, height, 0xcc175796);
        fillGradient(poseStack, previewRight, 0, width, height / 3, 0x5500cccc, 0x00000000);
        fillGradient(poseStack, previewRight, height - (height / 4), width, height, 0x00000000, 0x55000000);

        fillGradient(poseStack, 0, 0, previewRight, height / 3, 0xcc00cccc, 0x00000000);
        fillGradient(poseStack, 0, height - (height / 3), previewRight, height, 0x00000000, 0xcc000000);

        vLine(poseStack, previewRight - 2, -1, height, 0xFF005454);
        vLine(poseStack, previewRight - 1, -1, height, 0xFF00A8A8);
        vLine(poseStack, previewRight, -1, height, 0xFF005454);

        vLine(poseStack, 0, -1, height, 0xFF005454);
        vLine(poseStack, 1, -1, height, 0xFF00A8A8);
        vLine(poseStack, 2, -1, height, 0xFF005454);

        hLine(poseStack, 0, previewRight-1, 0, 0xFF005454);
        hLine(poseStack, 2, previewRight - 2, 1, 0xFF00A8A8);
        hLine(poseStack, 3, previewRight-3, 2, 0xFF005454);

        hLine(poseStack, 0, previewRight-1, height - 1, 0xFF005454);
        hLine(poseStack, 2, previewRight - 2, height - 2, 0xFF00A8A8);
        hLine(poseStack, 3, previewRight-3, height - 3, 0xFF005454);
    }

    public void setSelectedCategory(StyleLoader.Category category) {
        this.selectedCategory = category;
        this.styleWidget.updateButtons(category, StyleLoader.REGISTRY.values().stream()
                .filter(
                        style -> style.categories.contains(category)
                        && (this.unlockedStyles.contains(style.styleId) || (minecraft.player.isCreative() && minecraft.player.hasPermissions(2)))
                )
                .sorted(Comparator.comparing(o -> o.styleId.toString()))
                .toList());
    }
}
