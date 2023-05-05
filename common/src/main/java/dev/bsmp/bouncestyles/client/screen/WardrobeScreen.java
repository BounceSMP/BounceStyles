package dev.bsmp.bouncestyles.client.screen;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.screen.widgets.*;
import dev.bsmp.bouncestyles.data.StylePreset;
import dev.bsmp.bouncestyles.networking.packets.EquipStyleServerbound;
import dev.bsmp.bouncestyles.networking.packets.ToggleArmorVisibilityServerbound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.List;

public class WardrobeScreen extends Screen {
    private static final Identifier TEX_WIDGETS = new Identifier(BounceStyles.modId, "textures/gui/widgets.png");
    WardrobePreviewWidget previewWidget;
    WardrobeCategoryWidget categoryWidget;

    WardrobeStyleWidget styleWidget;
    WardrobePresetsWidget presetsWidget;

    WardrobeWidget activeWidget;
    TexturedButtonWidget clearButton;
    TexturedButtonWidget armorVisibilityButton;

    List<Identifier> unlockedStyles;
    StyleLoader.Category selectedCategory;
    int previewRight;
    int topBarHeight;

    public WardrobeScreen(List<Identifier> unlocks) {
        super(new LiteralText("Wardrobe Screen"));
        this.unlockedStyles = unlocks;
    }

    @Override
    protected void init() {
        super.init();
        this.previewRight = width / 3;
        this.topBarHeight = height / 10;

        this.previewWidget = addDrawableChild(new WardrobePreviewWidget(0, 0, previewRight, height, client.player));
        this.categoryWidget = addDrawableChild(new WardrobeCategoryWidget(this, previewRight, 1, width - previewRight - 48, topBarHeight));

        this.styleWidget = new WardrobeStyleWidget(previewRight, topBarHeight + 2, width - previewRight, height - topBarHeight);
        this.presetsWidget = new WardrobePresetsWidget(client, this, previewRight, topBarHeight, width - previewRight, height - topBarHeight, 30, topBarHeight);

        int btnSize = topBarHeight;
        this.clearButton = addDrawableChild(new ScaledImageButton(new LiteralText("Clear Equipped"), width - topBarHeight, 1, btnSize, btnSize, 98, 0, 24, 24, TEX_WIDGETS, button -> clearEquipped()));
        this.armorVisibilityButton = addDrawableChild(new ScaledImageButton(new LiteralText("Toggle Armor Visibility"),width - (topBarHeight * 2), 1, btnSize, btnSize, 122, 0, 24, 24, TEX_WIDGETS, button -> toggleArmor()));

        if(this.activeWidget instanceof WardrobeStyleWidget)
            this.activeWidget = this.styleWidget;
        else if(this.activeWidget instanceof WardrobePresetsWidget)
            this.activeWidget = this.presetsWidget;

        if(this.selectedCategory == null)
            this.setSelectedCategory(StyleLoader.Category.Head);
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        this.activeWidget.render(poseStack, mouseX, mouseY, partialTick);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(MatrixStack poseStack) {
        super.renderBackground(poseStack);

        fill(poseStack, 0, 0, width, height, 0xcc175796);
        fillGradient(poseStack, previewRight, 0, width, height / 3, 0x5500cccc, 0x00000000);
        fillGradient(poseStack, previewRight, height - (height / 4), width, height, 0x00000000, 0x55000000);

        fillGradient(poseStack, 0, 0, previewRight, height / 3, 0xcc00cccc, 0x00000000);
        fillGradient(poseStack, 0, height - (height / 3), previewRight, height, 0x00000000, 0xcc000000);

        drawVerticalLine(poseStack, previewRight - 2, -1, height, 0xFF005454);
        drawVerticalLine(poseStack, previewRight - 1, -1, height, 0xFF00A8A8);
        drawVerticalLine(poseStack, previewRight, -1, height, 0xFF005454);

        drawVerticalLine(poseStack, 0, -1, height, 0xFF005454);
        drawVerticalLine(poseStack, 1, -1, height, 0xFF00A8A8);
        drawVerticalLine(poseStack, 2, -1, height, 0xFF005454);

        drawHorizontalLine(poseStack, 0, previewRight-1, 0, 0xFF005454);
        drawHorizontalLine(poseStack, 2, previewRight - 2, 1, 0xFF00A8A8);
        drawHorizontalLine(poseStack, 3, previewRight-3, 2, 0xFF005454);

        drawHorizontalLine(poseStack, 0, previewRight-1, height - 1, 0xFF005454);
        drawHorizontalLine(poseStack, 2, previewRight - 2, height - 2, 0xFF00A8A8);
        drawHorizontalLine(poseStack, 3, previewRight-3, height - 3, 0xFF005454);
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
        if(this.presetsWidget != null && this.presetsWidget.isNarratable() && this.presetsWidget.needsRefreshing)
            this.presetsWidget.refreshEntries();
    }

    public void setSelectedCategory(StyleLoader.Category category) {
        this.selectedCategory = category;

        if (category == StyleLoader.Category.Preset)
            this.activeWidget = this.presetsWidget;
        else {
            this.activeWidget = this.styleWidget;
            this.styleWidget.updateButtons(
                    category, StyleLoader.REGISTRY.values().stream()
                            .filter(style -> style.categories.contains(category)
                                    && (this.unlockedStyles.contains(style.styleId) || (client.player.isCreative() && client.player.hasPermissionLevel(2)))
                            )
                            .sorted(Comparator.comparing(o -> o.styleId.toString()))
                            .toList()
            );
        }
    }

    public List<StylePreset> requestPresets() {
        return StyleLoader.PRESETS.values().stream()
                .filter(stylePreset -> (stylePreset.hasAllUnlocked(this.unlockedStyles)) || (client.player.isCreative() && client.player.hasPermissionLevel(2)))
                .sorted(Comparator.comparing(o -> o.presetId().toString()))
                .toList();
    }

    private void clearEquipped() {
        new EquipStyleServerbound(StyleLoader.Category.Head, null).sendToServer();
        new EquipStyleServerbound(StyleLoader.Category.Body, null).sendToServer();
        new EquipStyleServerbound(StyleLoader.Category.Legs, null).sendToServer();
        new EquipStyleServerbound(StyleLoader.Category.Feet, null).sendToServer();
    }

    private void toggleArmor() {
        new ToggleArmorVisibilityServerbound().sendToServer();
    }
}
