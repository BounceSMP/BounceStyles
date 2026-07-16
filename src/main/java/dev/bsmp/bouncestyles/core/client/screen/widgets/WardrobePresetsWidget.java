package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.Keybinds;
import dev.bsmp.bouncestyles.core.client.screen.widgets.button.WardrobeIconButton;
import dev.bsmp.bouncestyles.core.data.preset.ClientPresets;
import dev.bsmp.bouncestyles.api.style.StylePreset;
import dev.bsmp.bouncestyles.core.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.data.unlocks.UnlockManager;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.util.*;
import java.util.function.Supplier;
//? if >= 1.21.5 {
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//? } elif <= 1.21.1 {
/*import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
*///? }

public class WardrobePresetsWidget extends AbstractSelectionList<WardrobePresetsWidget.PresetEntry> implements WardrobeWidget {
    private final WardrobeScreen parentScreen;
    private final WardrobeIconButton createPresetButton;
    public EditBox nameEntry;

    boolean namingPreset;
    String bindingPreset;
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

        this.nameEntry = new EditBox(minecraft.font, x + 10 + buttonSize, y + height - buttonSize - 5, width - 20 - buttonSize, 20, Component.literal("Preset Name"));
        this.nameEntry.setHint(Component.literal("Preset Name..."));
        this.nameEntry.visible = false;

        this.createPresetButton = new WardrobeIconButton(x + 5, y + height - buttonSize - 5, "btn_create", button -> {
            if (!this.namingPreset) {
                this.nameEntry.visible = true;
            } else {
                this.nameEntry.visible = false;
                String name = this.nameEntry.getValue();
                if (!name.isBlank()) {
                    ClientPresets.createPreset(StyleData.getEntityData(minecraft.player), name);
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
        ClientPresets.getGlobalPresets().forEach((presetName, preset) -> {
            addEntry(new PresetEntry(this, presetName, preset, true));
        });
        ClientPresets.getPlayerPresets().forEach((presetName, preset) ->
                addEntry(new PresetEntry(this, presetName, preset, false))
        );
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
            this.createPresetButton.setMessage(Component.literal(s));
        }
    }

    //? if >= 1.21.11 {
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (isHovered()) {
            if (this.nameEntry.mouseClicked(event, doubleClick)) {
                this.nameEntry.setFocused(true);
                return true;
            }

            this.nameEntry.setFocused(false);

            if (!this.createPresetButton.mouseClicked(event, doubleClick))
                return super.mouseClicked(event, doubleClick);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.bindingPreset != null) {
            this.bindKey(event);
            return true;
        }
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

    //? if >= 1.21.5 {
    private void bindKey(KeyEvent event) {
        var key = event.isEscape() ? null : InputConstants.getKey(event);
        Keybinds.addPresetKeybind(this.bindingPreset, key);
        this.bindingPreset = null;
        this.refreshEntries();
    }
    //? } else {

    //? }

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
        return this.width - (this.width / 3);
    }

    public static class PresetEntry extends AbstractSelectionList.Entry<PresetEntry> {
        private static final Identifier TEX_GLOBAL = BounceStyles.id("textures/gui/sprites/global/global.png");

        WardrobePresetsWidget parentWidget;
        String presetName;
        StylePreset preset;

        boolean isGlobal;
        boolean isHovered = false;
        List<Component> tooltip;

        WardrobeIconButton deleteButton;
        PresetKeybindButton keybindButton;

        public PresetEntry(WardrobePresetsWidget parentWidget, String presetName, StylePreset preset, boolean global) {
            this.parentWidget = parentWidget;
            this.presetName = presetName;
            this.preset = preset;
            this.isGlobal = global;

            if (!global) {
                this.deleteButton = new WardrobeIconButton(0, 0, "btn_delete", button -> {
                    ClientPresets.deletePreset(presetName);
                    this.parentWidget.needsRefreshing = true;
                });
                this.deleteButton.setTooltip(Tooltip.create(Component.literal("Delete")));
            }

            if (UnlockManager.requiresUnlocks(Minecraft.getInstance().player)) {
                var errors = new ArrayList<Component>();
                this.preset.getAllNonEmpty().forEach((category, equippedStyle) -> {
                    var error = StylePreset.errorCheck(Minecraft.getInstance().player, equippedStyle);
                    if (error != StylePreset.Error.NO_ERROR) {
                        errors.add(Component.literal("(" + category.name() + ") " + error.message).withStyle(style -> style.withColor(ChatFormatting.GRAY)));
                    }
                });

                if (!errors.isEmpty()) {
                    errors.addFirst(Component.literal("Errors found with this preset").withStyle(style -> style.withColor(ChatFormatting.GRAY).withUnderlined(true)));
                    this.tooltip = errors;
                }
            }

            keybindButton = new PresetKeybindButton(Keybinds.getKeyForPreset(this.presetName), button -> {
                if (this.parentWidget.bindingPreset == null) {
                    this.parentWidget.bindingPreset = this.presetName;
                    button.setMessage(Component.literal("[ ... ]"));
                }
                else
                    this.parentWidget.bindingPreset = null;
            });
        }

        //? if >= 1.21.5 {
        @Override
        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            renderEntry(context, mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight(), partialTick);
        }
        //? } else {
        /*@Override
        public void render(GuiGraphics context, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            renderEntry(context, mouseX, mouseY, left, top, width, height, partialTick);
        }
        *///? }

        private void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, int left, int top, int width, int height, float partialTick) {
            this.isHovered = mouseX >= left && mouseX <= left + width && mouseY >= top && mouseY <= top + height;

            int colorBg = this.isHovered ? 0xFF2E4C6B : 0xFF0D2C4C;
            guiGraphics.fill(left, top, left + width, top + height, colorBg);

            int colorOutline = this.isHovered ? 0xFF00cccc : 0xFF00A8A8;
            guiGraphics.fill(left, top, left + width, top + 1, colorOutline); //Top Line
            guiGraphics.fill(left, top + height, left + width, top + height - 1, colorOutline); //Bottom Line

            guiGraphics.fill(left, top, left + 1, top + height, colorOutline); //Left Line
            guiGraphics.fill(left + width - 1, top, left + width, top + height, colorOutline); //Right Line

            guiGraphics.drawString(Minecraft.getInstance().font, this.presetName, left + 5, top + (height / 2) - 4, this.isHovered ? 0xFFb3fffe : 0xFFFFFFFF);

            this.keybindButton.setX(left + width + 5);
            this.keybindButton.setY(top);
            this.keybindButton.render(guiGraphics, mouseX, mouseY, partialTick);

            if (this.isGlobal) {
                WardrobeWidget.blit(guiGraphics, TEX_GLOBAL, left + width - 17, top + 2, 15, 15, 15, 15, 0, 0);
            }
            else {
                this.deleteButton.setX(this.keybindButton.getX() + this.keybindButton.getWidth() + 3);
                this.deleteButton.setY(top + 1);
                this.deleteButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            if (this.isHovered) {
                if (this.tooltip != null)
                    guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, this.tooltip, mouseX, mouseY);
            }
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.isHovered || this.keybindButton.isMouseOver (mouseX, mouseY) || !this.isGlobal && this.deleteButton.isMouseOver(mouseX, mouseY);
        }

        //? if >= 1.21.11 {
        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
            if (!this.keybindButton.mouseClicked(event, isDoubleClick)) {
                if (this.isGlobal || !this.deleteButton.mouseClicked(event, isDoubleClick)) {
                    new EquipStyleServerbound(this.preset.toMap()).sendToServer();
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                    return true;
                }
            }
            return false;
        }
        //? } else {
        /*@Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.keybindButton.mouseClicked(mouseX, mouseY, button)) {
                if (this.isGlobal || !this.deleteButton.mouseClicked(mouseX, mouseY, button)) {
                    new EquipStyleServerbound(this.preset.toMap()).sendToServer();
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                    return true;
                }
            }
            return false;
        }
        *///? }
    }

    private static class PresetKeybindButton extends Button {
        protected PresetKeybindButton(InputConstants.Key key, OnPress onPress) {
            super(0, 0, 60, 20, key != null ? Component.translatable(key.getName()) : Component.literal("[ ]"), onPress, PresetKeybindButton::createNarration);
        }

        @Override
        protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.isHovered = mouseX >= this.getX() && mouseX <= this.getX() + width && mouseY >= this.getY() && mouseY <= this.getY() + height;

            int colorBg = this.isHovered ? 0xFF2E4C6B : 0xFF0D2C4C;
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + width, this.getY() + height, colorBg);

            int colorOutline = this.isHovered ? 0xFF00cccc : 0xFF00A8A8;
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + width, this.getY() + 1, colorOutline); //Top Line
            guiGraphics.fill(this.getX(), this.getY() + height, this.getX() + width, this.getY() + height - 1, colorOutline); //Bottom Line

            guiGraphics.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + height, colorOutline); //Left Line
            guiGraphics.fill(this.getX() + width - 1, this.getY(), this.getX() + width, this.getY() + height, colorOutline); //Right Line

            var font = Minecraft.getInstance().font;
            guiGraphics.drawString(
                    font,
                    this.getMessage(),
                    this.getX() + (this.getWidth() / 2) - (font.width(this.getMessage()) / 2),
                    this.getY() + (this.getHeight() / 2) - 3,
                    this.isHovered ? 0xFFb3fffe : 0xFFFFFFFF
            );
        }

        private static MutableComponent createNarration(Supplier<MutableComponent> mutableComponentSupplier) {
            return mutableComponentSupplier.get();
        }
    }

    //? if <= 1.20.1 {
    /*@Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}
    *///?} else if >= 1.21.1 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    //?}
}
