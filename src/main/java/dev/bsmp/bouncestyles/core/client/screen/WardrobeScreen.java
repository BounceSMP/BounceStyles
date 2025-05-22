package dev.bsmp.bouncestyles.core.client.screen;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.client.screen.widgets.*;
import dev.bsmp.bouncestyles.api.style.StylePreset;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.ToggleArmorVisibilityServerbound;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class WardrobeScreen extends Screen {
    private static final ResourceLocation TEX_WIDGETS = BounceStyles.resourceLocation("textures/gui/widgets.png");
    WardrobePreviewWidget previewWidget;
    WardrobeCategoryWidget categoryWidget;

    WardrobeStyleWidget styleWidget;
    WardrobePresetsWidget presetsWidget;

    WardrobeWidget activeWidget;
    ImageButton clearButton;
    ImageButton armorVisibilityButton;

    List<ResourceLocation> unlockedStyles;
    BounceStylesRegistries.Category selectedCategory;
    int previewRight;
    int topBarHeight;

    public WardrobeScreen(List<ResourceLocation> unlocks) {
        super(Component.literal("Wardrobe Screen"));
        this.unlockedStyles = unlocks;
    }

    @Override
    protected void init() {
        super.init();
        this.previewRight = width / 3;
        this.topBarHeight = height / 10;

        this.previewWidget = addRenderableWidget(new WardrobePreviewWidget(0, 0, previewRight, height, minecraft.player));
        this.categoryWidget = addRenderableWidget(new WardrobeCategoryWidget(this, previewRight, 1, width - previewRight - 48, topBarHeight));

        this.styleWidget = new WardrobeStyleWidget(previewRight, topBarHeight + 2, width - previewRight, height - topBarHeight);
        this.presetsWidget = new WardrobePresetsWidget(minecraft, this, previewRight, topBarHeight, width - previewRight, height - topBarHeight, 30, topBarHeight);

        int btnSize = topBarHeight;
        this.clearButton = addRenderableWidget(new ScaledImageButton(Component.literal("Clear Equipped"), width - topBarHeight, 1, btnSize, btnSize, 98, 0, 24, 24, TEX_WIDGETS, button -> clearEquipped()));
        this.armorVisibilityButton = addRenderableWidget(new ScaledImageButton(Component.literal("Toggle Armor Visibility"),width - (topBarHeight * 2), 1, btnSize, btnSize, 122, 0, 24, 24, TEX_WIDGETS, button -> toggleArmor()));

        if(this.activeWidget instanceof WardrobeStyleWidget)
            this.activeWidget = this.styleWidget;
        else if(this.activeWidget instanceof WardrobePresetsWidget)
            this.activeWidget = this.presetsWidget;

        if(this.selectedCategory == null)
            this.setSelectedCategory(BounceStylesRegistries.Category.Head);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        renderBackground(context);
        this.activeWidget.render(context, mouseX, mouseY, partialTick);
        super.render(context, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics context) {
        super.renderBackground(context);

        context.fill(0, 0, width, height, 0xcc175796);
        context.fillGradient(previewRight, 0, width, height / 3, 0x5500cccc, 0x00000000);
        context.fillGradient(previewRight, height - (height / 4), width, height, 0x00000000, 0x55000000);

        context.fillGradient(0, 0, previewRight, height / 3, 0xcc00cccc, 0x00000000);
        context.fillGradient(0, height - (height / 3), previewRight, height, 0x00000000, 0xcc000000);

        context.vLine(previewRight - 2, -1, height, 0xFF005454);
        context.vLine(previewRight - 1, -1, height, 0xFF00A8A8);
        context.vLine(previewRight, -1, height, 0xFF005454);

        context.vLine(0, -1, height, 0xFF005454);
        context.vLine(1, -1, height, 0xFF00A8A8);
        context.vLine(2, -1, height, 0xFF005454);

        context.hLine(0, previewRight-1, 0, 0xFF005454);
        context.hLine(2, previewRight - 2, 1, 0xFF00A8A8);
        context.hLine(3, previewRight-3, 2, 0xFF005454);

        context.hLine(0, previewRight-1, height - 1, 0xFF005454);
        context.hLine(2, previewRight - 2, height - 2, 0xFF00A8A8);
        context.hLine(3, previewRight-3, height - 3, 0xFF005454);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.activeWidget.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.activeWidget.mouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.activeWidget.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        this.activeWidget.charTyped(chr, modifiers);
        return super.charTyped(chr, modifiers);
    }

    @Override
    public void tick() {
        if(this.presetsWidget != null && this.presetsWidget.isActive() && this.presetsWidget.needsRefreshing)
            this.presetsWidget.refreshEntries();
    }

    public void setSelectedCategory(BounceStylesRegistries.Category category) {
        this.selectedCategory = category;

        if (category == BounceStylesRegistries.Category.Preset)
            this.activeWidget = this.presetsWidget;
        else {
            this.activeWidget = this.styleWidget;
            this.styleWidget.updateButtons(
                    category, BounceStylesRegistries.getAllStyles().stream()
                            .filter(style -> style.getCategories().contains(category)
                                    && (this.unlockedStyles.contains(style.getStyleId()) || (minecraft.player.isCreative() && minecraft.player.hasPermissions(2)))
                            )
                            .sorted(Comparator.comparing(o -> o.getStyleId().toString()))
                            .toList()
            );
        }
    }

    public List<StylePreset> requestPresets() {
        return BounceStylesRegistries.PRESETS.values().stream().toList();
    }

    private void clearEquipped() {
        new EquipStyleServerbound(BounceStylesRegistries.Category.Head, Optional.empty()).sendToServer();
        new EquipStyleServerbound(BounceStylesRegistries.Category.Body, Optional.empty()).sendToServer();
        new EquipStyleServerbound(BounceStylesRegistries.Category.Legs, Optional.empty()).sendToServer();
        new EquipStyleServerbound(BounceStylesRegistries.Category.Feet, Optional.empty()).sendToServer();
    }

    private void toggleArmor() {
        new ToggleArmorVisibilityServerbound().sendToServer();
    }
}
