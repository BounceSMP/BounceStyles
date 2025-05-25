package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.platform.Platform;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries.Category;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WardrobeStyleWidget extends AbstractWidget implements WardrobeWidget {
    private static final ResourceLocation TEX_WIDGETS = BounceStyles.resourceLocation("textures/gui/widgets.png");

    Category category;
    @Nullable StyleButton selectedStyleButton;
    List<Style> styles = new ArrayList<>();
    List<StyleButton> buttons = new ArrayList<>();

    boolean updateButtons = false;
    boolean updateVisible = false;
    List<StyleButton> visibleButtons = new ArrayList<>();

    int buttonSize = 50;
    int margin = 3;
    float previewRotation = -30f;
    int scroll = 0;

    int left;
    int top;

    int rows;
    int columns;

    public WardrobeStyleWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal("Wardrobe Selection"));
        this.left = x + 5;
        this.top = y + 2;
        this.rows = this.height / (buttonSize + margin);
        this.columns = (this.width - 5) / (buttonSize + margin);

        updateButtons(Category.Head, new ArrayList<>());
    }

    public void updateButtons(Category category, List<Style> styles) {
        this.scroll = 0;
        this.category = category;
        this.buttons.clear();
        this.styles = new ArrayList<>(styles);

        this.updateButtons = true;
    }

    private void updateButtons() {
        StyleData styleData = StyleData.getOrCreateStyleData(Minecraft.getInstance().player);

        for (Style style : styles) {
            StyleButton button = new StyleButton(this, 0, 0, buttonSize, buttonSize, style);

            var equippedStyle = styleData.getStyleForSlot(category);
            if (equippedStyle.isPresent() && equippedStyle.get().getFirst() == style) {
                if (style.getTextureVariants().isPresent()) button.textureId = equippedStyle.get().getSecond();
                this.selectedStyleButton = button;
            }

            this.buttons.add(button);
        }

        this.updateVisibleButtons();
        this.updateButtons = false;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        if (Platform.isDevelopmentEnvironment())
            context.drawString(Minecraft.getInstance().font, Minecraft.getInstance().fpsString, 5, 5, 0xFFFFFF);

        if (this.updateButtons)
            this.updateButtons();
        if (this.updateVisible)
            this.updateVisibleButtons();

        this.previewRotation = this.previewRotation + (partialTick * 0.05f);

        StyleButton tooltipButton = null;

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        for (StyleButton button : this.visibleButtons) {
            button.renderWidget(context, bufferSource, mouseX, mouseY, partialTick);
            if (button.isHovered())
                tooltipButton = button;
        }

        if (tooltipButton != null) {
            tooltipButton.renderTooltip(context, mouseX, mouseY);
        }

        int maxScroll = getTotalRows() - rows;
        int maxPosition = maxScroll * (buttonSize + margin);
        int barWidth = 6;
        int barLeft = getX() + width - barWidth - 3;
        int barTop = getY() + 10;
        int barHeight = height - 20;
        int barBottom = barTop + barHeight;

        if (this.buttons.size() > this.rows * this.columns) {
            int scrollHeight = (int) ((float) (barHeight * barHeight) / (float) maxPosition);
            int scrollTop = this.scroll * (barBottom - barTop - scrollHeight) / maxScroll + barTop;

            context.fill(barLeft - 1, barTop - 1, barLeft + barWidth + 1, barTop + barHeight + 1, 0xFF00a8a8);
            context.fill(barLeft, barTop, barLeft + barWidth, barTop + barHeight, 0xFF212121);
            context.fill(barLeft + 1, scrollTop + 1, barLeft + barWidth - 1, scrollTop + scrollHeight - 1, 0xFF0092c5);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isValidClickButton(mouseButton) && this.clicked(mouseX, mouseY)) {
            for (StyleButton button : this.visibleButtons)
                if(button.mouseClicked(mouseX, mouseY, mouseButton))
                    return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.scroll = Math.min(Math.max(this.scroll - (int) delta, 0), getTotalRows() - this.rows);
        this.updateVisible = true;
        return true;
    }

    private void updateVisibleButtons() {
        this.visibleButtons.clear();

        int index = 0;
        int startingIndex = this.scroll * this.columns;
        int endIndex = startingIndex + (this.rows * this.columns);

        for (int i = Math.max(this.scroll * this.columns, 0); i < endIndex; i++) {
            if (i < this.buttons.size()) {
                StyleButton button = this.buttons.get(i);
                setButtonPosition(button, index);
                this.visibleButtons.add(button);
            }
            index++;
        }

        this.updateVisible = false;
    }

    private void setButtonPosition(StyleButton button, int index) {
        int col = index % columns;
        int row = index / columns;
        button.setPosition(this.left + (col * (buttonSize + margin)), this.top + (row * (buttonSize + margin)));
    }

    private int getTotalRows() {
        return (this.buttons.size() / this.columns) + 1;
    }

    public static class StyleButton extends Button {
        private WardrobeStyleWidget parentWidget;
        private List<Component> tooltip;
        private Style style;
        private int textureId = -1;

        public StyleButton(WardrobeStyleWidget parentWidget, int x, int y, int width, int height, Style style) {
            super(x, y, width, height, Component.empty(), null, DEFAULT_NARRATION);
            this.parentWidget = parentWidget;
            this.tooltip = createTooltip(style, parentWidget.category);
            this.style = style;
        }

        private static List<Component> createTooltip(Style style, Category category) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable(style.getStyleId().getNamespace()+"."+style.getStyleId().getPath()+"."+category.name().toLowerCase()).withStyle(ChatFormatting.BOLD));
//            style.getTextureVariants().ifPresent(variants -> list.add(Component.literal(variants.size() + " Variants Available")));
            style.getCredits().ifPresent(credits -> {
                list.add(Component.literal("-Made By-").withStyle(ChatFormatting.GRAY));
                credits.forEach(s -> list.add(Component.literal(s).withStyle(ChatFormatting.GRAY)));
            });
            return list;
        }

        public void renderWidget(GuiGraphics context, MultiBufferSource.BufferSource bufferSource, int mouseX, int mouseY, float partialTick) {
            Window window = Minecraft.getInstance().getWindow();
            double guiScale = window.getGuiScale();

            this.isHovered = this.isMouseOver(mouseX, mouseY);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            context.blit(TEX_WIDGETS, this.getX(), this.getY(), this.width, this.height, 0, getYOffset() * 50,  50, 50, 256, 256);
            context.blit(TEX_WIDGETS, this.getX(), this.getY(), this.width, this.height, 0, getYOffset() * 50,  50, 50, 256, 256);

            if(!isHovered()) {
                int sOffset = width / 6;
                int sSize = sOffset * 2;
                RenderSystem.enableScissor(
                        (int) ((getX() + sOffset) * guiScale),
                        (int) ((window.getGuiScaledHeight() - getY() - height + sOffset) * guiScale),
                        (int) ((width - sSize) * guiScale),
                        (int) (((height - sSize) * guiScale))
                );
            }

            PoseStack poseStack = RenderSystem.getModelViewStack();
            poseStack.pushPose();
            poseStack.translate(getX() + (this.width / 2), getY() + this.height, 1050.0);
            poseStack.scale(1.0f, 1.0f, -1.0f);
            RenderSystem.applyModelViewMatrix();
            PoseStack poseStack2 = new PoseStack();
            float offsetY = switch (this.parentWidget.category) {
                case Head -> -(float)(.5f);
                case Body -> -(1f);
                case Legs, Feet -> -1.8F;
                case Preset -> 0.0F;
            };
            poseStack2.translate(0.0, 0, 1000.0);
            if(isHovered()) {
                poseStack2.scale((float) (height * 0.7), (float) (height * 0.7), 10f);
            }
            else {
                poseStack2.scale((float) (height * 0.6), (float) (height * 0.6), 10f);
            }
            poseStack2.translate(0.0, offsetY, 0.0);
            Quaternionf quaternion = new Quaternionf().rotateZ((float) Math.PI);
            quaternion.rotateY(this.parentWidget.previewRotation);
            poseStack2.mulPose(quaternion);
            Lighting.setupForEntityInInventory();
            BounceStylesClient.STYLE_RENDERER.renderStyle(
                    poseStack2,
                    this.style,
                    this.textureId,
                    this.parentWidget.category,
                    bufferSource,
                    0f,
                    partialTick,
                    0xF000F0,
                    true
            );
            bufferSource.endBatch();
            poseStack.popPose();
            RenderSystem.applyModelViewMatrix();
            Lighting.setupFor3DItems();

            if(!isHovered())
                RenderSystem.disableScissor();
        }

        @Override
        public void onPress() {
            if(this.parentWidget.selectedStyleButton == this) {
                new EquipStyleServerbound(this.parentWidget.category, Optional.empty()).sendToServer();
                this.parentWidget.selectedStyleButton = null;
            }
            else {
                new EquipStyleServerbound(this.parentWidget.category, this.style != null ? Optional.of(this.style.getStyleId()) : Optional.empty()).sendToServer();
                this.parentWidget.selectedStyleButton = this;
            }
        }

        public void renderTooltip(GuiGraphics poseStack, int mouseX, int mouseY) {
            WardrobeWidget.drawTooltipStatic(poseStack, Minecraft.getInstance().font, this.tooltip, mouseX + 3, mouseY, parentWidget.getX() + parentWidget.width);
        }

        private int getYOffset() {
            return this.parentWidget.selectedStyleButton == this ? 2 : isHovered ? 1 : 0;
        }
    }
}
