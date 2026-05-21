package dev.bsmp.bouncestyles.core.client.screen.widgets;

import dev.bsmp.bouncestyles.core.data.Category;
import dev.bsmp.bouncestyles.core.data.StylePreset;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.StyleLoader;
import dev.bsmp.bouncestyles.core.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;

//? if <= 1.21.1 {
/*import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
*///? }

public class WardrobePresetsWidget extends AbstractSelectionList<WardrobePresetsWidget.PresetEntry> implements WardrobeWidget {
    private static final Identifier TEX_WIDGETS = BounceStyles.id("textures/gui/widgets.png");
    private static final Identifier TEX_ERROR = BounceStyles.id("textures/icon/error.png");
    private static final Identifier TEX_BTN_DELETE = BounceStyles.id("textures/gui/btn_delete.png");
    private static final Identifier TEX_BTN_DELETE_HOVER = BounceStyles.id("textures/gui/btn_delete_hover.png");
    private static final Identifier TEX_BTN_CREATE = BounceStyles.id("textures/gui/btn_create.png");
    private static final Identifier TEX_BTN_CREATE_HOVER = BounceStyles.id("textures/gui/btn_create_hover.png");

    private final WardrobeScreen parentScreen;
    private final WardrobeIconButton createPresetButton;
    public EditBox nameEntry;

    boolean namingPreset;
    public boolean needsRefreshing;

    public WardrobePresetsWidget(Minecraft minecraft, WardrobeScreen parentScreen, int x, int y, int width, int height, int itemHeight, int buttonSize) {
        //? if <= 1.20.1 {
        /*super(minecraft, width, height, y, y + height, itemHeight);
        this.x0 = x;
        this.x1 = x + width;

        setRenderBackground(false);
        setRenderTopAndBottom(false);
        *///?} else if >= 1.21.1 {
        super(minecraft, width, height, y, itemHeight);
        this.setX(x);
        //?}

        this.parentScreen = parentScreen;

        this.nameEntry = new EditBox(minecraft.font, x + 10 + buttonSize, y + height - buttonSize - 4, width - 20 - buttonSize, buttonSize - 2, Component.literal("Preset Name"));
        this.nameEntry.visible = false;

        this.createPresetButton = new WardrobeIconButton(x + 5, y + height - buttonSize - 5, buttonSize, buttonSize, TEX_BTN_CREATE, TEX_BTN_CREATE_HOVER, button -> {
            if (!this.namingPreset) {
                this.nameEntry.visible = true;
            } else {
                this.nameEntry.visible = false;
                String name = this.nameEntry.getValue();
                if (!name.isBlank()) {
                    BounceStylesRegistries.createPreset(StyleData.getOrCreateStyleData(minecraft.player), name);
                    refreshEntries();
                }
                this.nameEntry.setValue("");
            }
            this.namingPreset = !this.namingPreset;
        });

        refreshEntries();
    }

    public void refreshEntries() {
        this.needsRefreshing = false;
        clearEntries();
        for (StylePreset preset : this.parentScreen.requestPresets()) {
            addEntry(new PresetEntry(this, preset));
        }
    }

    //? if <= 1.20.1 {
    /*@Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderPresetList(guiGraphics, mouseX, mouseY, partialTick);
    }
    *///?} else if >= 1.21.1 {
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        renderPresetList(guiGraphics, mouseX, mouseY, partialTick);
    }
    //?}

    private void renderPresetList(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.nameEntry.render(guiGraphics, mouseX, mouseY, partialTick);
        this.createPresetButton.render(guiGraphics, mouseX, mouseY, partialTick);
        if(this.createPresetButton.isMouseOver(mouseX, mouseY)) {
            String s = "Create Preset";
            if (this.namingPreset) {
                if (this.nameEntry.getValue().isBlank())
                    s = "Cancel";
                else
                    s = "Save";
            }

            //? if >= 1.21.11 {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, List.of(ClientTooltipComponent.create(Component.literal(s).getVisualOrderText())), mouseX, mouseY, (screenWidth, screenHeight, mouseX1, mouseY1, tooltipWidth, tooltipHeight) -> {
                return new Vector2i();
            }, null);
            //? } else
            //drawTooltip(guiGraphics, Minecraft.getInstance().font, Component.literal(s), mouseX, mouseY);
        }
    }

    //? if >= 1.21.11 {

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.nameEntry.mouseClicked(event, doubleClick)) {
            this.nameEntry.setFocused(true);
            return true;
        }
        this.createPresetButton.mouseClicked(event, doubleClick);
        boolean b = super.mouseClicked(event, doubleClick);
        this.setFocused(null);
        this.setSelected(null);
        return b;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.nameEntry.keyPressed(event))
            return true;
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (this.nameEntry.charTyped(event))
            return true;
        return super.charTyped(event);
    }

    //? } else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.nameEntry.mouseClicked(mouseX, mouseY, button)) {
            this.nameEntry.setFocused(true);
            return true;
        }
        this.createPresetButton.mouseClicked(mouseX, mouseY, button);
        boolean b = super.mouseClicked(mouseX, mouseY, button);
        this.setFocused(null);
        this.setSelected(null);
        return b;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(this.nameEntry.keyPressed(keyCode, scanCode, modifiers))
            return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(this.nameEntry.charTyped(chr, modifiers))
            return true;
        return super.charTyped(chr, modifiers);
    }
    *///? }

    //? if >= 1.21.11 {
    @Override
    protected int scrollBarY() {
        return super.scrollBarY();
    }
    //? } else {
    /*@Override
    protected int getScrollbarPosition() {
        //? if <= 1.20.1 {
        /^return this.x1 - 5;
        ^///?} else if >= 1.21.1 {
        return this.getX() + this.getWidth() - 5;
        //?}
    }
    *///? }

    @Override
    public int getRowLeft() {
        //? if <= 1.20.1 {
        /*return this.x0 + 5;
        *///?} else if >= 1.21.1 {
        return this.getX() + 5;
        //?}
    }

    @Override
    public int getRowWidth() {
        return this.width - ((this.width / 10) * 2);
    }

    public static class PresetEntry extends AbstractSelectionList.Entry<PresetEntry> {
        private static List<Component> tooltipLines;
        WardrobePresetsWidget parentWidget;
        StylePreset preset;
        WardrobeIconButton deleteButton;

        boolean isHovered = false;

        public PresetEntry(WardrobePresetsWidget parentWidget, StylePreset preset) {
            this.parentWidget = parentWidget;
            this.preset = preset;
            this.deleteButton = new WardrobeIconButton(0,0, 24,24, TEX_BTN_DELETE, TEX_BTN_DELETE_HOVER, button -> {
                StyleLoader.removePreset(this.preset.presetId());
                this.parentWidget.needsRefreshing = true;
            });

            tooltipLines = List.of(Component.literal("One or more items in this preset"), Component.literal("are not unlocked or invalid!"));
        }

        @Override
        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            renderEntry(context, mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight(), partialTick);
        }

        //? if <= 1.21.1 {
        /*@Override
        public void render(GuiGraphics context, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            renderEntry(context, mouseX, mouseY, left, top, width, height, partialTick);
        }
        *///? }

        private void renderEntry(GuiGraphics context, int mouseX, int mouseY, int left, int top, int width, int height, float partialTick) {
            this.isHovered = mouseX >= left && mouseX <= left + width && mouseY >= top && mouseY <= top + height;

            int colorBg = this.isHovered ? 0xFF2E4C6B : 0xFF0D2C4C;
            context.fill(left, top, left + width, top + height, colorBg);

            int colorOutline = this.isHovered ? 0xFF00cccc : 0xFF00A8A8;
            context.fill(left, top, left + width, top + 1, colorOutline); //Top Line
            context.fill(left, top + height, left + width, top + height - 1, colorOutline); //Bottom Line

            context.fill(left, top, left + 1, top + height, colorOutline); //Left Line
            context.fill(left + width - 1, top, left + width, top + height, colorOutline); //Right Line

            context.drawString(Minecraft.getInstance().font, this.preset.name(), left + 5, top + (height / 2) - 4, this.isHovered ? 0xb3fffe : 0xFFFFFF);
            this.deleteButton.setX(left + width + 2);
            this.deleteButton.setY(top + 1);
            this.deleteButton.render(context, mouseX, mouseY, partialTick);

            //? if <= 1.21.1 {
            /*if(preset.error()) {
                PoseStack poseStack = context.pose();
                context.blit(TEX_ERROR, left + width - 16, top + 5, 0, 0, 16, 16, 16, 16);
                if(this.isHovered) {
                    poseStack.pushPose();
                    GlStateManager._enableDepthTest();
                    poseStack.translate(0, 0, 100);
                    WardrobeWidget.drawTooltipStatic(context, Minecraft.getInstance().font, tooltipLines, mouseX, mouseY);
                    poseStack.popPose();
                }
            }
            *///? }
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.isHovered;
        }

        //? if >= 1.21.11 {
        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
            if(this.isHovered) {
                new EquipStyleServerbound(Map.of(
                        Category.Head, preset.head(),
                        Category.Body, preset.body(),
                        Category.Legs, preset.legs(),
                        Category.Feet, preset.feet()
                )).sendToServer();
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                return true;
            }
            return this.deleteButton.mouseClicked(event, isDoubleClick);
        }
        //? } else {
        /*@Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(this.isHovered) {
                new EquipStyleServerbound(Map.of(
                        Category.Head, preset.head(),
                        Category.Body, preset.body(),
                        Category.Legs, preset.legs(),
                        Category.Feet, preset.feet()
                )).sendToServer();
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                return true;
            }
            return this.deleteButton.mouseClicked(mouseX, mouseY, button);
        }
        *///? }
    }

    //? if <= 1.20.1 {
    /*@Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}
    *///?} else if >= 1.21.1 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    //?}
}
