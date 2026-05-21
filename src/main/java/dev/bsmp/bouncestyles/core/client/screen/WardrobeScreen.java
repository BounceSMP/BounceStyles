package dev.bsmp.bouncestyles.core.client.screen;

import dev.bsmp.bouncestyles.core.data.Category;
import dev.bsmp.bouncestyles.core.data.StylePreset;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import dev.bsmp.bouncestyles.core.client.screen.widgets.*;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WardrobeScreen extends Screen {
    private static final Identifier TEX_CLEAR = BounceStyles.id("textures/gui/btn_clear.png");
    private static final Identifier TEX_CLEAR_HOVER = BounceStyles.id("textures/gui/btn_clear_hover.png");
    private static final Identifier TEX_CATEGORY = BounceStyles.id("textures/gui/selection_category.png");

    WardrobePreviewWidget previewWidget;
    WardrobeStyleSelectionWidget styleWidget;
    WardrobePresetsWidget presetsWidget;

    WardrobeWidget activeWidget;
    IconSelectionButton categoryBtn;
    EditBox searchBox;
    WardrobeIconButton clearButton;

    List<Identifier> unlockedStyles;
    Category selectedCategory;
    int previewRight;
    int topBarHeight;

    public WardrobeScreen(List<Identifier> unlocks) {
        super(Component.literal("Wardrobe Screen"));
        this.unlockedStyles = unlocks;
    }

    @Override
    protected void init() {
        super.init();
        this.previewRight = width / 3;
        this.topBarHeight = height / 10;

        this.categoryBtn = addRenderableOnly(new IconSelectionButton(previewRight + 5, 2, 24, 24, TEX_CATEGORY, false, Component.literal("Category")));
        for (Category category : Category.values()) {
            this.categoryBtn.addItem(Component.literal(category.name()), () -> this.setSelectedCategory(category));
        }

        this.previewWidget = addRenderableWidget(new WardrobePreviewWidget(0, 0, previewRight, height, minecraft.player));
        this.styleWidget = new WardrobeStyleSelectionWidget(previewRight, topBarHeight + 2, width - previewRight, height - topBarHeight);
        this.presetsWidget = new WardrobePresetsWidget(minecraft, this, previewRight, topBarHeight + 4, width - previewRight, height - topBarHeight, 30, topBarHeight);

        this.searchBox = addRenderableWidget(new EditBox(minecraft.font, previewRight + 32, 4, 150, 20, Component.empty()));
        this.searchBox.setResponder(s -> this.updateStyles());
        this.searchBox.setHint(Component.literal("Search..."));

        this.clearButton = addRenderableWidget(new WardrobeIconButton(width - topBarHeight, 2, 24, 24, TEX_CLEAR, TEX_CLEAR_HOVER, Component.literal("Clear Equipped"), button -> clearEquipped()));

        if(this.activeWidget instanceof WardrobeStyleSelectionWidget) {
            this.searchBox.visible = true;
            this.activeWidget = this.styleWidget;
        }
        else if(this.activeWidget instanceof WardrobePresetsWidget) {
            this.searchBox.visible = false;
            this.activeWidget = this.presetsWidget;
        }

        this.setSelectedCategory(this.selectedCategory != null ? this.selectedCategory : Category.Head);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        renderWardrobeBackground(context);
        if (this.activeWidget != null)
            this.activeWidget.render(context, mouseX, mouseY, partialTick);
        //? if < 1.21.11 {
        /*context.pose().pushPose();
        context.pose().translate(0, 0, 1200);
        super.render(context, mouseX, mouseY, partialTick);
        context.pose().popPose();
        *///? } else {
        context.pose().pushMatrix();
        super.render(context, mouseX, mouseY, partialTick);
        context.pose().popMatrix();
        //? }
    }

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

    //? if >= 1.21.11 {
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!this.categoryBtn.mouseClicked(event, doubleClick))
            this.activeWidget.mouseClicked(event, doubleClick);
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.activeWidget.keyPressed(event))
            return true;
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        this.activeWidget.charTyped(event);
        return super.charTyped(event);
    }
    //? } else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.categoryBtn.mouseClicked(mouseX, mouseY, button))
            this.activeWidget.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

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
    *///? }

    //? if <= 1.20.1 {
    
    /*@Override
    public void renderBackground(GuiGraphics context) {}

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.activeWidget.mouseScrolled(mouseX, mouseY, amount);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    *///?} else if >= 1.21.1 {
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.activeWidget.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    //?}

    @Override
    public void tick() {
        if(this.presetsWidget != null && this.presetsWidget.isActive() && this.presetsWidget.needsRefreshing)
            this.presetsWidget.refreshEntries();
    }

    public void setSelectedCategory(Category category) {
        this.selectedCategory = category;

        if (category == Category.Preset) {
            this.searchBox.visible = false;
            this.activeWidget = this.presetsWidget;
        }
        else {
            this.searchBox.visible = true;
            updateStyles();
        }
    }

    private void updateStyles() {
        CompletableFuture.supplyAsync(() -> BounceStylesRegistries.getAllStyles().stream()
            //? if >= 1.21.11 {
            .filter(style -> style.getCategories().contains(this.selectedCategory) && (this.unlockedStyles.contains(style.getStyleId()) || (minecraft.player.isCreative() && minecraft.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))))
            //? } else
//            .filter(style -> style.getCategories().contains(this.selectedCategory) && (this.unlockedStyles.contains(style.getStyleId()) || (minecraft.player.isCreative() && minecraft.player.hasPermissions(2))))
            .filter(style -> {
                var label = Component.translatable(style.getStyleId().getNamespace()+"."+style.getStyleId().getPath()+"."+this.selectedCategory.name().toLowerCase());
                return label.getString().toLowerCase().contains(this.searchBox.getValue().toLowerCase());
            })
            .sorted(Comparator.comparing(o -> o.getStyleId().toString()))
            .toList()
        ).thenApply(styles -> {
            this.activeWidget = this.styleWidget;
            this.styleWidget.updateButtons(this.selectedCategory, styles);
            return null;
        });
    }

    public List<StylePreset> requestPresets() {
        return BounceStylesClient.PRESETS.values().stream().toList();
    }

    private void clearEquipped() {
        new EquipStyleServerbound(Category.Head).sendToServer();
        new EquipStyleServerbound(Category.Body).sendToServer();
        new EquipStyleServerbound(Category.Legs).sendToServer();
        new EquipStyleServerbound(Category.Feet).sendToServer();
    }

//    public static void renderPlayerInGUI(GuiGraphics guiGraphics, float x, float y) {
//        Window window = Minecraft.getInstance().getWindow();
//        double guiScale = window.getGuiScale();
//        var poseStack = RenderSystem.getModelViewStack();
//        /*? if <= 1.20.1 {*/  /*poseStack.pushPose();  *//*?} else if >= 1.21.1 {*/ poseStack.pushMatrix(); /*?}*/
//        poseStack.translate(x, y, 1050);
//        poseStack.scale(1f, 1f, -1f);
//        RenderSystem.applyModelViewMatrix();
//        PoseStack poseStack2 = new PoseStack();
//        poseStack2.translate(0.0, getY(), 1000.0);
//        poseStack2.scale((float) ((window.getHeight() / 3) / guiScale), (float) ((window.getHeight() / 3) / guiScale), 1);
//        Quaternionf quaternion = new Quaternionf().rotateZ((float) Math.PI);
//        Quaternionf quaternion2 = new Quaternionf().rotateY(previewRotation);
//        quaternion.mul(quaternion2);
//        poseStack2.mulPose(quaternion);
//        float h = this.previewPlayer.yBodyRot;
//        float i = this.previewPlayer.getYRot();
//        float j = this.previewPlayer.getXRot();
//        float k = this.previewPlayer.yHeadRotO;
//        float l = this.previewPlayer.yHeadRot;
//        this.previewPlayer.yBodyRot = 160f;
//        this.previewPlayer.setYRot(160.0f);
//        this.previewPlayer.setXRot(0f);
//        this.previewPlayer.yHeadRot = this.previewPlayer.getYRot();
//        this.previewPlayer.yHeadRotO = this.previewPlayer.getYRot();
//        Lighting.setupForEntityInInventory(); //Setup Entity Lighting
//        EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
//        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
//        quaternion2.conjugate();
//        renderDispatcher.overrideCameraOrientation(quaternion2);
//        renderDispatcher.setRenderShadow(false);
//        RenderSystem.runAsFancy(() -> renderDispatcher.render(this.previewPlayer, 0, 0, 0, 0, 1f, poseStack2, bufferSource, 0xF000F0));
//        bufferSource.endBatch();
//        renderDispatcher.setRenderShadow(true);
//        this.previewPlayer.yBodyRot = h;
//        this.previewPlayer.setYRot(i);
//        this.previewPlayer.setXRot(j);
//        this.previewPlayer.yHeadRotO = k;
//        this.previewPlayer.yHeadRot = l;
//        /*? if <= 1.20.1 {*/  /*poseStack.popPose();  *//*?} else if >= 1.21.1 {*/ poseStack.popMatrix(); /*?}*/
//        RenderSystem.applyModelViewMatrix();
//        Lighting.setupFor3DItems();
//    }

    public static void renderStyleInGUI(GuiGraphics guiGraphics, double x, double y) {

    }
}
