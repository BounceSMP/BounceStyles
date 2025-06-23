package dev.bsmp.bouncestyles.core.client.screen;

import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.api.style.StylePreset;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.client.screen.widgets.*;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.ToggleArmorVisibilityServerbound;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WardrobeScreen extends Screen {
    private static final ResourceLocation TEX_CLEAR = BounceStyles.resourceLocation("textures/gui/btn_clear.png");
    private static final ResourceLocation TEX_CLEAR_HOVER = BounceStyles.resourceLocation("textures/gui/btn_clear_hover.png");
    private static final ResourceLocation TEX_CATEGORY = BounceStyles.resourceLocation("textures/gui/selection_category.png");
    private static final ResourceLocation TEX_ARMOR = BounceStyles.resourceLocation("textures/gui/selection_armor.png");

    WardrobePreviewWidget previewWidget;
    IconSelectionButton categoryWidget;

    WardrobeStyleSelectionWidget styleWidget;
    WardrobePresetsWidget presetsWidget;

    WardrobeWidget activeWidget;
    WardrobeIconButton clearButton;
    IconSelectionButton armorVisibilityButton;

    List<ResourceLocation> unlockedStyles;
    Category selectedCategory;
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
        this.categoryWidget = addRenderableOnly(new IconSelectionButton(previewRight + 5, 2, 24, 24, TEX_CATEGORY, 96, 72, false, Component.literal("Category")));
        for (Category category : Category.values()) {
            if (category == Category.Preset) continue;
            this.categoryWidget.addItem(Component.literal(category.name()), () -> this.setSelectedCategory(category));
        }

        this.styleWidget = new WardrobeStyleSelectionWidget(previewRight, topBarHeight + 2, width - previewRight, height - topBarHeight);
        this.presetsWidget = new WardrobePresetsWidget(minecraft, this, previewRight, topBarHeight, width - previewRight, height - topBarHeight, 30, topBarHeight);

        int btnSize = topBarHeight;
        this.clearButton = addRenderableWidget(new WardrobeIconButton(width - topBarHeight, 2, btnSize, btnSize, TEX_CLEAR, TEX_CLEAR_HOVER, Component.literal("Clear Equipped"), button -> clearEquipped()));
        this.armorVisibilityButton = addRenderableOnly(new IconSelectionButton(width - 50, 2, 24, 24, TEX_ARMOR, 120, 72, true, Component.literal("Toggle Armor Visibility")));
        for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            this.armorVisibilityButton.addItem(Component.literal(slot.getName()), () -> this.toggleArmor(slot.getIndex()));
        }

        if(this.activeWidget instanceof WardrobeStyleSelectionWidget)
            this.activeWidget = this.styleWidget;
        else if(this.activeWidget instanceof WardrobePresetsWidget)
            this.activeWidget = this.presetsWidget;

        this.setSelectedCategory(this.selectedCategory != null ? this.selectedCategory : Category.Head);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        renderWardrobeBackground(context);
        if (this.activeWidget != null)
            this.activeWidget.render(context, mouseX, mouseY, partialTick);
        super.render(context, mouseX, mouseY, partialTick);
    }

    //? if <= 1.20.1 {
    /*@Override
    public void renderBackground(GuiGraphics context) {
    }
    *///?} else if >= 1.21.1 {
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }
    //?}

    private void renderWardrobeBackground(GuiGraphics context) {
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

    public void refresh() {
        if (this.activeWidget instanceof WardrobeStyleSelectionWidget widget)
            widget.refresh();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.categoryWidget.mouseClicked(mouseX, mouseY, button))
            if (!this.armorVisibilityButton.mouseClicked(mouseX, mouseY, button))
                this.activeWidget.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    //? if <= 1.20.1 {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.activeWidget.mouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    *///?} else if >= 1.21.1 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.activeWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    //?}

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.activeWidget.keyPressed(keyCode, scanCode, modifiers))
            return true;
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

    public void setSelectedCategory(Category category) {
        this.selectedCategory = category;

        if (category == Category.Preset)
            this.activeWidget = this.presetsWidget;
        else {
            CompletableFuture.supplyAsync(() -> BounceStylesRegistries.getAllStyles().stream()
                .filter(style -> style.getCategories().contains(category) &&
                        (this.unlockedStyles.contains(style.getStyleId()) || (minecraft.player.isCreative() && minecraft.player.hasPermissions(2)))
                )
                //ToDo Throw in a filter based on search bar when that's implemented
                .sorted(Comparator.comparing(o -> o.getStyleId().toString()))
                .toList()
            ).thenApply(styles -> {
                this.activeWidget = this.styleWidget;
                this.styleWidget.updateButtons(category, styles);
                return null;
            });
        }
    }

    public List<StylePreset> requestPresets() {
        return BounceStylesRegistries.PRESETS.values().stream().toList();
    }

    private void clearEquipped() {
        new EquipStyleServerbound(Category.Head).sendToServer();
        new EquipStyleServerbound(Category.Body).sendToServer();
        new EquipStyleServerbound(Category.Legs).sendToServer();
        new EquipStyleServerbound(Category.Feet).sendToServer();
    }

    private void toggleArmor(int index) {
        new ToggleArmorVisibilityServerbound(index).sendToServer();
    }
}
