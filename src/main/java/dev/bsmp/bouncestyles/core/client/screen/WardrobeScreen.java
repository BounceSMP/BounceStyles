package dev.bsmp.bouncestyles.core.client.screen;

import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.client.screen.widgets.WardrobePresetsWidget;
import dev.bsmp.bouncestyles.core.client.screen.widgets.WardrobePreviewWidget;
import dev.bsmp.bouncestyles.core.client.screen.widgets.WardrobeStyleSelectionWidget;
import dev.bsmp.bouncestyles.core.client.screen.widgets.WardrobeWidget;
import dev.bsmp.bouncestyles.core.client.screen.widgets.button.WardrobeIconButton;
import dev.bsmp.bouncestyles.core.data.Category;
import dev.bsmp.bouncestyles.core.data.Style;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import net.minecraft.client.gui.ComponentPath;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WardrobeScreen extends Screen {
    WardrobeWidget activeWidget;
    WardrobePreviewWidget previewWidget;
    WardrobeStyleSelectionWidget styleWidget;
    WardrobePresetsWidget presetsWidget;

    EditBox searchBox;
    Map<Category, WardrobeIconButton> categoryButtons = new HashMap<>();

    int previewRight;
    int topBarHeight;
    List<Identifier> unlockedStyles;

    public WardrobeScreen(List<Identifier> unlocks) {
        super(Component.literal("Wardrobe Screen"));
        this.unlockedStyles = unlocks;
    }

    @Override
    protected void init() {
        super.init();
        this.previewRight = width / 3;
        this.topBarHeight = height / 10;

        var selectedCategory = this.styleWidget != null ? this.styleWidget.getCategory() : Category.Head;

        this.previewWidget = addRenderableWidget(new WardrobePreviewWidget(0, 0, previewRight, height, minecraft.player));
        this.styleWidget = new WardrobeStyleSelectionWidget(previewRight, topBarHeight + 2, width - previewRight, height - topBarHeight);
        this.presetsWidget = new WardrobePresetsWidget(minecraft, this, previewRight, topBarHeight + 4, width - previewRight, height - topBarHeight, 30, topBarHeight);

        int y = 2;
        var i = width - previewRight - (50 + (Category.values().length * 22));
        this.searchBox = addRenderableWidget(new EditBox(minecraft.font, previewRight + 4, y, i, 20, Component.empty()));
        this.searchBox.setResponder(s -> this.updateStyles(this.styleWidget.getCategory()));
        this.searchBox.setHint(Component.literal("Search..."));

        for (int index = 0; index < Category.values().length; index++) {
            Category category = Category.values()[index];
            this.categoryButtons.put(category, addRenderableWidget(new WardrobeIconButton(
                    (width - 44) - ((Category.values().length - index) * 22), y,
                    "btn_"+category.getSerializedName(),
                    Component.literal(category.name()),
                    button -> this.updateStyles(category)
            )));
        }

        addRenderableWidget(new WardrobeIconButton(width - 44, y, "btn_preset", Component.literal("Presets"), btn -> {
            this.activeWidget = this.presetsWidget;
        }));
        addRenderableWidget(new WardrobeIconButton(width - 22, y, "btn_clear", Component.literal("Clear Equipped"), button -> clearEquipped()));
        if(this.activeWidget instanceof WardrobePresetsWidget) {
            this.searchBox.visible = false;
            this.activeWidget = this.presetsWidget;
        }
        else {
            this.searchBox.visible = true;
            this.activeWidget = this.styleWidget;
        }

        this.updateStyles(selectedCategory);
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
        if (!this.activeWidget.mouseClicked(event, doubleClick))
            return super.mouseClicked(event, doubleClick);
        return false;
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

    private void updateStyles(Category category) {
        this.categoryButtons.get(this.styleWidget.getCategory()).setFocused(false);

        CompletableFuture.supplyAsync(() -> BounceStylesRegistries.getAllStyles().stream()
            .filter(style -> availabilityFilter(style, category))
            .filter(style -> searchFilter(style, category))
            .sorted(Comparator.comparing(style -> style.getStyleId().toString()))
            .toList()
        ).thenApply(styles -> {
            this.activeWidget = this.styleWidget;
            this.styleWidget.updateButtons(category, styles);
            return null;
        });

        this.categoryButtons.get(category).setFocused(true);
    }

    private boolean availabilityFilter(Style style, Category category) {
        boolean categoryCheck = style.getCategories().contains(category);
        boolean unlockCheck = this.unlockedStyles.contains(style.getStyleId());
        //? if >= 1.21.11 {
        boolean permissionCheck = (minecraft.player.isCreative() && minecraft.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER));
        //? } else
//        boolean permissionCheck = (minecraft.player.isCreative() && minecraft.player.hasPermissions(2));
        return  categoryCheck && (unlockCheck || permissionCheck);
    }

    private boolean searchFilter(Style style, Category category) {
        var label = Component.translatable(style.getStyleId().getNamespace()+"."+style.getStyleId().getPath()+"."+category.getSerializedName());
        return label.getString().toLowerCase().contains(this.searchBox.getValue().toLowerCase());
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
}
