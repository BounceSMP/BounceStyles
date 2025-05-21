package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.StyleLoader;
import dev.bsmp.bouncestyles.core.StyleRegistry;
import dev.bsmp.bouncestyles.core.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.data.StylePreset;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

public class WardrobePresetsWidget extends AbstractSelectionList<WardrobePresetsWidget.PresetEntry> implements WardrobeWidget {
    private static final ResourceLocation TEX_WIDGETS = new ResourceLocation(BounceStyles.modId, "textures/gui/widgets.png");

    private final WardrobeScreen parentScreen;
    private final ScaledImageButton createPresetButton;
    public EditBox presetNameEntry;

    boolean namingPreset;
    public boolean needsRefreshing;

    public WardrobePresetsWidget(Minecraft minecraft, WardrobeScreen parentScreen, int x, int y, int width, int height, int itemHeight, int buttonSize) {
        super(minecraft, width, height, y, y + height, itemHeight);
        this.x0 = x;
        this.x1 = x + width;

        this.parentScreen = parentScreen;

        this.presetNameEntry = new EditBox(minecraft.font, x + 10 + buttonSize, y + height - buttonSize - 5, width - 20 - buttonSize, buttonSize, Component.literal("Preset Name"));
        this.presetNameEntry.visible = false;
        this.presetNameEntry.active = false;

        this.createPresetButton = new ScaledImageButton(null, x + 5, y + height - buttonSize - 5, buttonSize, buttonSize, 74, 0, 24, 24, TEX_WIDGETS, button -> {
            if(!this.namingPreset) {
                this.presetNameEntry.visible = true;
                this.presetNameEntry.active = true;
            }
            else {
                this.presetNameEntry.visible = false;
                this.presetNameEntry.active = false;
                String name = this.presetNameEntry.getValue();
                if(!name.isBlank()) {
                    StyleRegistry.createPreset(StyleData.getOrCreateStyleData(minecraft.player), name);
                    refreshEntries();
                }
                this.presetNameEntry.setValue("");
            }
            this.namingPreset = !this.namingPreset;
        });

        setRenderBackground(false);
        setRenderTopAndBottom(false);
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
    public void render(GuiGraphics poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        this.presetNameEntry.render(poseStack, mouseX, mouseY, partialTick);
        this.createPresetButton.render(poseStack, mouseX, mouseY, partialTick);
        if(this.createPresetButton.isMouseOver(mouseX, mouseY)) {
            String s = "Create Preset";
            if (this.namingPreset) {
                if (this.presetNameEntry.getValue().isBlank())
                    s = "Cancel";
                else
                    s = "Save";
            }

            drawTooltip(Component.literal(s), mouseX, mouseY, Minecraft.getInstance().font, poseStack, 0);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY) && !this.createPresetButton.mouseClicked(mouseX, mouseY, button) && !this.presetNameEntry.mouseClicked(mouseX, mouseY, button)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(this.presetNameEntry.keyPressed(keyCode, scanCode, modifiers))
            return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(this.presetNameEntry.charTyped(chr, modifiers))
            return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x1 - 5;
    }

    @Override
    public int getRowLeft() {
        return this.x0 + 5;
    }

    @Override
    public int getRowWidth() {
        return this.width - ((this.width / 10) * 2);
    }

    public static class PresetEntry extends AbstractSelectionList.Entry<PresetEntry> {
        private static List<FormattedCharSequence> tooltipLines;
        WardrobePresetsWidget parentWidget;
        StylePreset preset;
        ImageButton deleteButton;

        boolean isHovered = false;

        public PresetEntry(WardrobePresetsWidget parentWidget, StylePreset preset) {
            this.parentWidget = parentWidget;
            this.preset = preset;
            this.deleteButton = new ImageButton(0,0,24,24,50,0, TEX_WIDGETS, button -> {
                StyleLoader.removePreset(this.preset.presetId());
                this.parentWidget.needsRefreshing = true;
            });

//            if(tooltipLines == null)
                tooltipLines = Minecraft.getInstance().font.split(FormattedText.of("One or more items in this preset are not unlocked or invalid!"), 165);
        }

        @Override
        public void render(GuiGraphics context, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            isMouseOver = this.isHovered = mouseX >= left && mouseX <= left + width && mouseY >= top && mouseY <= top + height;

            int colorBg = isMouseOver ? 0xFF2E4C6B : 0xFF0D2C4C;
            context.fill(left, top, left + width, top + height, colorBg);

            int colorOutline = isMouseOver ? 0xFF00cccc : 0xFF00A8A8;
            context.fill(left, top, left + width, top + 1, colorOutline); //Top Line
            context.fill(left, top + height, left + width, top + height - 1, colorOutline); //Bottom Line

            context.fill(left, top, left + 1, top + height, colorOutline); //Left Line
            context.fill(left + width - 1, top, left + width, top + height, colorOutline); //Right Line

            context.drawString(Minecraft.getInstance().font, this.preset.name(), left + 5, top + (height / 2) - 4, isMouseOver ? 0xb3fffe : 0xFFFFFF);
            this.deleteButton.setX(left + width + 5);
            this.deleteButton.setY(top + 1);
            this.deleteButton.render(context, mouseX, mouseY, partialTick);

            if(preset.error()) {
                PoseStack poseStack = context.pose();
//                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                context.blit(TEX_WIDGETS, left + width - 22, top + 2, 50, 48, 22, 22, 256, 256);
                if(isMouseOver) {
                    poseStack.pushPose();
                    GlStateManager._enableDepthTest();
                    poseStack.translate(0, 0, 100);
                    WardrobeWidget.drawTooltipBackgroundStatic(context, mouseX + 5, mouseY - 12, 168, (tooltipLines.size() * 10) + 7);
                    int i = 0;
                    for(FormattedCharSequence text : tooltipLines) {
                        context.drawString(Minecraft.getInstance().font, text, mouseX + 9, mouseY - 7 + (i * 10), 0xFFFFFF, false);
                        i++;
                    }
                    poseStack.popPose();
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
                new EquipStyleServerbound(StyleRegistry.Category.Head, StyleRegistry.getStyle(preset.headId())).sendToServer();
                new EquipStyleServerbound(StyleRegistry.Category.Body, StyleRegistry.getStyle(preset.bodyId())).sendToServer();
                new EquipStyleServerbound(StyleRegistry.Category.Legs, StyleRegistry.getStyle(preset.legsId())).sendToServer();
                new EquipStyleServerbound(StyleRegistry.Category.Feet, StyleRegistry.getStyle(preset.feetId())).sendToServer();
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                return true;
            }
            return this.deleteButton.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}
