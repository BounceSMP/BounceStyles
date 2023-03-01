package dev.bsmp.bouncestyles.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.screen.widgets.*;
import dev.bsmp.bouncestyles.data.StylePreset;
import dev.bsmp.bouncestyles.networking.EquipStyleC2S;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class WardrobeScreen extends Screen {
    private static final ResourceLocation TEX_WIDGETS = new ResourceLocation(BounceStyles.modId, "textures/gui/widgets.png");

    WardrobeCategoryWidget categoryWidget;
    WardrobeStyleWidget styleWidget;
    WardrobePreviewWidget previewWidget;
    WardrobePresetsWidget presetsWidget;

    ImageButton clearButton;
    public EditBox presetName;

    List<ResourceLocation> unlockedStyles;
    StyleLoader.Category selectedCategory;

    int previewRight;
    int topBarHeight;

    public WardrobeScreen(List<ResourceLocation> unlocks) {
        super(new TextComponent("Wardrobe Screen"));
        this.unlockedStyles = unlocks;
    }

    @Override
    protected void init() {
        super.init();
        this.previewRight = width / 3;
        this.topBarHeight = height / 10;
        this.previewWidget = addRenderableWidget(new WardrobePreviewWidget(0, 0, previewRight, height, minecraft.player));
        this.categoryWidget = addRenderableWidget(new WardrobeCategoryWidget(this, previewRight, 1, width - previewRight - 24, topBarHeight));
        this.styleWidget = addRenderableWidget(new WardrobeStyleWidget(previewRight, topBarHeight, width - previewRight, height - topBarHeight));
        this.clearButton = addRenderableWidget(new ScaledImageButton(width - topBarHeight, 0, topBarHeight, topBarHeight, 98, 0, 24, 24, TEX_WIDGETS, button -> clear()));
        this.presetName = addRenderableWidget(new EditBox(minecraft.font, previewRight + topBarHeight + 10, height - topBarHeight - 5, (width - previewRight) / 2, topBarHeight, new TextComponent("Preset Name")));
        this.presetName.visible = false;
        this.presetName.active = false;
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

    @Override
    public void tick() {
        if(this.presetsWidget != null && this.presetsWidget.isActive() && this.presetsWidget.needsRefreshing)
            this.presetsWidget.refreshEntries();
    }

    private void clear() {
        EquipStyleC2S.sendToServer(StyleLoader.Category.Head, null);
        EquipStyleC2S.sendToServer(StyleLoader.Category.Body, null);
        EquipStyleC2S.sendToServer(StyleLoader.Category.Legs, null);
        EquipStyleC2S.sendToServer(StyleLoader.Category.Feet, null);
    }

    public void setSelectedCategory(StyleLoader.Category category) {
        this.selectedCategory = category;
        if (category == StyleLoader.Category.Preset) {
            this.styleWidget.active = false;
            this.styleWidget.visible = false;
            this.presetsWidget = addRenderableWidget(new WardrobePresetsWidget(minecraft, this, previewRight, topBarHeight, width - previewRight, height - topBarHeight, 30, topBarHeight));
        }
        else {
            removeWidget(this.presetsWidget);
            this.presetsWidget = null;
            this.presetName.visible = false;
            this.presetName.active = false;
            this.presetName.setValue("");

            this.styleWidget.active = true;
            this.styleWidget.visible = true;
            this.styleWidget.updateButtons(
                    category, StyleLoader.REGISTRY.values().stream()
                    .filter(style -> style.categories.contains(category)
                                  && (this.unlockedStyles.contains(style.styleId) || (minecraft.player.isCreative() && minecraft.player.hasPermissions(2)))
                    )
                    .sorted(Comparator.comparing(o -> o.styleId.toString()))
                    .toList()
            );
        }
    }

    public List<StylePreset> requestPresets() {
        return StyleLoader.PRESETS.values().stream()
                .filter(stylePreset -> (stylePreset.hasAllUnlocked(this.unlockedStyles)) || (minecraft.player.isCreative() && minecraft.player.hasPermissions(2)))
                .sorted(Comparator.comparing(o -> o.presetId().toString()))
                .toList();
    }
}
