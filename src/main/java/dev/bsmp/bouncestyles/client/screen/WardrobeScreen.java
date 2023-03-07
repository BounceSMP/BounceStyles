package dev.bsmp.bouncestyles.client.screen;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.screen.widgets.*;
import dev.bsmp.bouncestyles.data.StylePreset;
import dev.bsmp.bouncestyles.networking.EquipStyleC2S;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import dev.bsmp.bouncestyles.networking.ToggleArmorVisibilityC2S;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WardrobeScreen extends Screen {
    private static final Identifier TEX_WIDGETS = new Identifier(BounceStyles.modId, "textures/gui/widgets.png");

    WardrobeCategoryWidget categoryWidget;
    WardrobeStyleWidget styleWidget;
    WardrobePreviewWidget previewWidget;
    WardrobePresetsWidget presetsWidget;

    TexturedButtonWidget clearButton;
    TexturedButtonWidget toggleArmorButton;
    public TextFieldWidget presetName;

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
        this.styleWidget = addDrawableChild(new WardrobeStyleWidget(previewRight, topBarHeight, width - previewRight, height - topBarHeight));

        int btnSize = topBarHeight;
        this.clearButton = addDrawableChild(new ScaledImageButton(new LiteralText("Clear Equipped"), width - topBarHeight, 1, btnSize, btnSize, 98, 0, 24, 24, TEX_WIDGETS, button -> clear()));
        this.toggleArmorButton = addDrawableChild(new ScaledImageButton(new LiteralText("Toggle Armor Visibility"),width - (topBarHeight * 2), 1, btnSize, btnSize, 122, 0, 24, 24, TEX_WIDGETS, button -> toggleArmor()));

        this.presetName = addDrawableChild(new TextFieldWidget(client.textRenderer, previewRight + topBarHeight + 10, height - topBarHeight - 5, (width - previewRight) / 2, topBarHeight, new LiteralText("Preset Name")));
        this.presetName.visible = false;
        this.presetName.active = false;
        this.setSelectedCategory(StyleLoader.Category.Head);
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
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
    public void tick() {
        if(this.presetsWidget != null && this.presetsWidget.isNarratable() && this.presetsWidget.needsRefreshing)
            this.presetsWidget.refreshEntries();
    }

    private void clear() {
        EquipStyleC2S.sendToServer(StyleLoader.Category.Head, null);
        EquipStyleC2S.sendToServer(StyleLoader.Category.Body, null);
        EquipStyleC2S.sendToServer(StyleLoader.Category.Legs, null);
        EquipStyleC2S.sendToServer(StyleLoader.Category.Feet, null);
    }

    private void toggleArmor() {
        ToggleArmorVisibilityC2S.sendToServer();
    }

    public void setSelectedCategory(StyleLoader.Category category) {
        this.selectedCategory = category;
        if (category == StyleLoader.Category.Preset) {
            this.styleWidget.active = false;
            this.styleWidget.visible = false;
            this.presetsWidget = addDrawableChild(new WardrobePresetsWidget(client, this, previewRight, topBarHeight, width - previewRight, height - topBarHeight, 30, topBarHeight));
        }
        else {
            remove(this.presetsWidget);
            this.presetsWidget = null;
            this.presetName.visible = false;
            this.presetName.active = false;
            this.presetName.setText("");

            this.styleWidget.active = true;
            this.styleWidget.visible = true;
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

    public static void drawTooltip(Text text, int x, int y, TextRenderer font, MatrixStack poseStack, int right) {
        if(right <= 0) right = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int textWidth = font.getWidth(text) + 3;
        int textX = x + 4 + textWidth > right ? x + (right - (x + textWidth)) - 2 : x + 2;

        poseStack.translate(0, 0, 100);

        fill(poseStack, textX, y - 11, x + textWidth + 3, y + 1, 0xFF000000);

        fill(poseStack, textX, y - 12, x + textWidth + 3, y - 11, 0xFF00A8A8);
        fill(poseStack, textX, y + 1, x + textWidth + 3, y + 2, 0xFF00A8A8);

        fill(poseStack, textX, y - 12, textX + 1, y + 2, 0xFF00A8A8);
        fill(poseStack, textX + textWidth + 1, y - 12, textX + textWidth + 2, y + 2, 0xFF00A8A8);

        drawTextWithShadow(poseStack, font, text, textX + 3, y - 9, 0xFFFFFF);
    }
}
