package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.data.PlayerStyleData;
import dev.bsmp.bouncestyles.data.StylePreset;
import dev.bsmp.bouncestyles.networking.EquipStyleC2S;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.List;

public class WardrobePresetsWidget extends AbstractSelectionList<WardrobePresetsWidget.PresetEntry> {
    private static final ResourceLocation TEX_WIDGETS = new ResourceLocation(BounceStyles.modId, "textures/gui/widgets.png");

    private WardrobeScreen parentScreen;
    private ScaledImageButton createButton;
    boolean namingPreset;
    public boolean needsRefreshing;

    public WardrobePresetsWidget(Minecraft minecraft, WardrobeScreen parentScreen, int x, int y, int width, int height, int itemHeight, int buttonSize) {
        super(minecraft, width, height, y, y + height, itemHeight);
        this.x0 = x;
        this.x1 = x + width;

        this.parentScreen = parentScreen;
        this.createButton = new ScaledImageButton(x + 5, y + height - buttonSize - 5, buttonSize, buttonSize, 74, 0, 24, 24, TEX_WIDGETS, button -> {
            if(!this.namingPreset) {
                this.parentScreen.presetName.visible = true;
                this.parentScreen.presetName.active = true;
                this.namingPreset = true;
            }
            else {
                this.parentScreen.presetName.visible = false;
                this.parentScreen.presetName.active = false;
                this.namingPreset = false;
                String name = this.parentScreen.presetName.getValue();
                if(!name.isBlank()) {
                    StyleLoader.createPreset(PlayerStyleData.getPlayerData(minecraft.player), name);
                    refreshEntries();
                }
                this.parentScreen.presetName.setValue("");
            }
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        this.createButton.render(poseStack, mouseX, mouseY, partialTick);
        if(this.createButton.isMouseOver(mouseX, mouseY)) {
            String s = "Create Preset";
            if (this.namingPreset) {
                if (this.parentScreen.presetName.getValue().isBlank())
                    s = "Cancel";
                else
                    s = "Save";
            }
            fill(poseStack, mouseX + 1, mouseY - 11, mouseX + minecraft.font.width(s) + 1, mouseY - 2, 0xFF000000);
            drawString(poseStack, minecraft.font, s, mouseX + 2, mouseY - 10, 0xFFFFFF);
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
        }

        @Override
        public void render(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            isMouseOver = this.isHovered = mouseX >= left && mouseX <= left + width && mouseY >= top && mouseY <= top + height;

            int colorBg = isMouseOver ? 0xFF2E4C6B : 0xFF0D2C4C;
            fill(poseStack, left, top, left + width, top + height, colorBg);

            int colorOutline = isMouseOver ? 0xFF00cccc : 0xFF00A8A8;
            fill(poseStack, left, top, left + width, top + 1, colorOutline); //Top Line
            fill(poseStack, left, top + height, left + width, top + height - 1, colorOutline); //Bottom Line

            fill(poseStack, left, top, left + 1, top + height, colorOutline); //Left Line
            fill(poseStack, left + width - 1, top, left + width, top + height, colorOutline); //Right Line

            drawString(poseStack, Minecraft.getInstance().font, this.preset.name(), left + 5, top + (height / 2) - 4, isMouseOver ? 0xb3fffe : 0xFFFFFF);
            this.deleteButton.x = left + width + 5;
            this.deleteButton.y = top + 1;
            this.deleteButton.render(poseStack, mouseX, mouseY, partialTick);

            if(preset.error()) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, TEX_WIDGETS);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                GuiComponent.blit(poseStack, left + width - 22, top + 2, 50, 48, 22, 22, 256, 256);
                if(isMouseOver) {
                    String s = "One or more items in this preset are not unlocked or invalid!";
                    fill(poseStack, mouseX + 1, mouseY - 11, mouseX + Minecraft.getInstance().font.width(s) + 1, mouseY - 2, 0xFF000000);
                    drawString(poseStack, Minecraft.getInstance().font, s, mouseX + 2, mouseY - 10, 0xFFFFFF);
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
                EquipStyleC2S.sendToServer(StyleLoader.Category.Head, StyleLoader.getStyle(preset.headId()));
                EquipStyleC2S.sendToServer(StyleLoader.Category.Body, StyleLoader.getStyle(preset.bodyId()));
                EquipStyleC2S.sendToServer(StyleLoader.Category.Legs, StyleLoader.getStyle(preset.legsId()));
                EquipStyleC2S.sendToServer(StyleLoader.Category.Feet, StyleLoader.getStyle(preset.feetId()));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                return true;
            }
            return this.deleteButton.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}
}
