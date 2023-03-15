package dev.bsmp.bouncestyles.client.screen.widgets;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.data.StylePreset;
import dev.bsmp.bouncestyles.mixin.EntryListAccessor;
import dev.bsmp.bouncestyles.networking.BounceStylesNetwork;
import dev.bsmp.bouncestyles.networking.EquipStyleC2S;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class WardrobePresetsWidget extends EntryListWidget<WardrobePresetsWidget.PresetEntry> {
    private static final Identifier TEX_WIDGETS = new Identifier(BounceStyles.modId, "textures/gui/widgets.png");

    private WardrobeScreen parentScreen;
    private ScaledImageButton createButton;
    boolean namingPreset;
    public boolean needsRefreshing;

    public WardrobePresetsWidget(MinecraftClient minecraft, WardrobeScreen parentScreen, int x, int y, int width, int height, int itemHeight, int buttonSize) {
        super(minecraft, width, height, y, y + height, itemHeight);
        this.left = x;
        this.right = x + width;

        this.parentScreen = parentScreen;
        this.createButton = new ScaledImageButton(null, x + 5, y + height - buttonSize - 5, buttonSize, buttonSize, 74, 0, 24, 24, TEX_WIDGETS, button -> {
            if(!this.namingPreset) {
                this.parentScreen.presetName.visible = true;
                this.parentScreen.presetName.active = true;
                this.namingPreset = true;
            }
            else {
                this.parentScreen.presetName.visible = false;
                this.parentScreen.presetName.active = false;
                this.namingPreset = false;
                String name = this.parentScreen.presetName.getText();
                if(!name.isEmpty()) {
                    StyleLoader.createPreset(StyleData.getPlayerData(minecraft.player), name);
                    refreshEntries();
                }
                this.parentScreen.presetName.setText("");
            }
        });

//        setRenderBackground(false);

        ((EntryListAccessor)this).setField_26846(false);
        ((EntryListAccessor)this).setField_26847(false);
        setRenderHeader(false, 0);
        refreshEntries();
    }

    public void refreshEntries() {
        this.needsRefreshing = false;
        clearEntries();
        for(StylePreset preset : this.parentScreen.requestPresets()) {
            addEntry(new PresetEntry(this, preset));
        }
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        this.createButton.render(poseStack, mouseX, mouseY, partialTick);
        if(this.createButton.isMouseOver(mouseX, mouseY)) {
            String s = "Create Preset";
            if (this.namingPreset) {
                if (this.parentScreen.presetName.getText().isEmpty())
                    s = "Cancel";
                else
                    s = "Save";
            }

            WardrobeScreen.drawTooltip(new LiteralText(s), mouseX, mouseY, MinecraftClient.getInstance().textRenderer, poseStack, 0);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY) && !this.createButton.mouseClicked(mouseX, mouseY, button)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return true;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.right - 5;
    }

    @Override
    public int getRowLeft() {
        return this.left + 5;
    }

    @Override
    public int getRowWidth() {
        return this.width - ((this.width / 10) * 2);
    }

    public static class PresetEntry extends EntryListWidget.Entry<PresetEntry> {
        WardrobePresetsWidget parentWidget;
        StylePreset preset;
        TexturedButtonWidget deleteButton;

        boolean isHovered = false;

        public PresetEntry(WardrobePresetsWidget parentWidget, StylePreset preset) {
            this.parentWidget = parentWidget;
            this.preset = preset;
            this.deleteButton = new TexturedButtonWidget(0,0, 24,24, 50,0, 24, TEX_WIDGETS, button -> {
                StyleLoader.removePreset(this.preset.presetId);
                this.parentWidget.needsRefreshing = true;
            });
        }

        @Override
        public void render(MatrixStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            isMouseOver = this.isHovered = mouseX >= left && mouseX <= left + width && mouseY >= top && mouseY <= top + height;

            int colorBg = isMouseOver ? 0xFF2E4C6B : 0xFF0D2C4C;
            fill(poseStack, left, top, left + width, top + height, colorBg);

            int colorOutline = isMouseOver ? 0xFF00cccc : 0xFF00A8A8;
            fill(poseStack, left, top, left + width, top + 1, colorOutline); //Top Line
            fill(poseStack, left, top + height, left + width, top + height - 1, colorOutline); //Bottom Line

            fill(poseStack, left, top, left + 1, top + height, colorOutline); //Left Line
            fill(poseStack, left + width - 1, top, left + width, top + height, colorOutline); //Right Line

            drawStringWithShadow(poseStack, MinecraftClient.getInstance().textRenderer, this.preset.name, left + 5, top + (height / 2) - 4, isMouseOver ? 0xb3fffe : 0xFFFFFF);
            this.deleteButton.x = left + width + 5;
            this.deleteButton.y = top + 1;
            this.deleteButton.render(poseStack, mouseX, mouseY, partialTick);

            if(preset.error) {
                MinecraftClient.getInstance().getTextureManager().bindTexture(TEX_WIDGETS);
                DrawableHelper.drawTexture(poseStack, left + width - 22, top + 2, 50, 48, 22, 22, 256, 256);
                if(isMouseOver) {
                    String s = "One or more items in this preset are not unlocked or invalid!";
                    fill(poseStack, mouseX + 1, mouseY - 11, mouseX + MinecraftClient.getInstance().textRenderer.getWidth(s) + 1, mouseY - 2, 0xFF000000);
                    drawStringWithShadow(poseStack, MinecraftClient.getInstance().textRenderer, s, mouseX + 2, mouseY - 10, 0xFFFFFF);
                }
            }
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.isHovered;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(this.isHovered) {
                BounceStylesNetwork.NETWORK.sendToServer(new EquipStyleC2S(StyleLoader.Category.Head, StyleLoader.getStyle(preset.headId)));
                BounceStylesNetwork.NETWORK.sendToServer(new EquipStyleC2S(StyleLoader.Category.Body, StyleLoader.getStyle(preset.bodyId)));
                BounceStylesNetwork.NETWORK.sendToServer(new EquipStyleC2S(StyleLoader.Category.Legs, StyleLoader.getStyle(preset.legsId)));
                BounceStylesNetwork.NETWORK.sendToServer(new EquipStyleC2S(StyleLoader.Category.Feet, StyleLoader.getStyle(preset.feetId)));
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                return true;
            }
            return this.deleteButton.mouseClicked(mouseX, mouseY, button);
        }
    }
}
