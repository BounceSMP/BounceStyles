package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
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

public abstract class WardrobeScrollWidget extends AbstractWidget {
    private static final ResourceLocation TEX_WIDGETS = BounceStyles.resourceLocation("textures/gui/widgets.png");

    protected static final int buttonSize = 50;
    protected static final int  margin = 3;

    protected int scroll = 0;
    protected int rows;
    protected int columns;
    protected int left;
    protected int top;
    float previewRotation = -30f;
    protected @Nullable StyleButton selectedStyleButton;

    protected boolean updateButtons = false;
    protected boolean updateVisible = false;
    protected List<Style> styles = new ArrayList<>();
    protected List<StyleButton> buttons = new ArrayList<>();
    protected List<StyleButton> visibleButtons = new ArrayList<>();

    public WardrobeScrollWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.rows = this.height / (buttonSize + margin);
        this.columns = (this.width - 5) / (buttonSize + margin);
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        this.previewRotation = this.previewRotation + (partialTick * 0.05f);

        if (this.updateButtons)
            this.updateButtons();
        if (this.updateVisible)
            this.updateVisibleButtons();

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


        if (this.buttons.size() > this.rows * this.columns) {
            int barWidth = 6;
            int barLeft = getX() + width - barWidth - 3;
            int barTop = getY() + 10;
            int barHeight = height - 20;
            int barBottom = barTop + barHeight;

            int maxScroll = getTotalRows() - rows;
            int scrollHeight = barHeight / maxScroll;
            int scrollTop = this.scroll * (barBottom - barTop - scrollHeight) / maxScroll + barTop;

            context.fill(barLeft - 1, barTop - 1, barLeft + barWidth + 1, barTop + barHeight + 1, 0xFF00a8a8);
            context.fill(barLeft, barTop, barLeft + barWidth, barTop + barHeight, 0xFF212121);
            context.fill(barLeft + 1, scrollTop + 1, barLeft + barWidth - 1, scrollTop + scrollHeight - 1, 0xFF0092c5);
        }
    }

    protected abstract void onSelectionClicked(StyleButton button);
    protected abstract void updateButtons();

    protected void updateVisibleButtons() {
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.scroll = Math.min(Math.max(this.scroll - (int) delta, 0), getTotalRows() - this.rows);
        this.updateVisible = true;
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (StyleButton button : this.visibleButtons)
            if(button.mouseClicked(mouseX, mouseY, mouseButton))
                return true;
        return false;
    }

    private int getTotalRows() {
        return (this.buttons.size() / this.columns) + 1;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    public static class StyleButton extends Button {
        private WardrobeScrollWidget parentWidget;
        private List<Component> tooltip;
        private Category category;
        private Style style;
        private int textureId = -1;

        public StyleButton(WardrobeScrollWidget parentWidget, int x, int y, int width, int height, Category category, Style style) {
            super(x, y, width, height, Component.empty(), null, DEFAULT_NARRATION);
            this.parentWidget = parentWidget;
            this.category = category;
            this.style = style;
            this.tooltip = createTooltip(style, category);
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
            if (style.getTextureVariants().isPresent())
                context.blit(TEX_WIDGETS, this.getX() + (this.getWidth() / 2) - 5, this.getY() + this.getHeight() - 8, 50, 48 + (getYOffset() * 7),  10, 7);

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
            //ToDo Consider an alternate way to adjust position
            float offsetY = switch (this.category) {
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
                    this.category,
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
            this.parentWidget.onSelectionClicked(this);
        }

        public Style getStyle() {
            return this.style;
        }

        public int getTextureId() {
            return this.textureId;
        }

        public void setTextureId(int id) {
            this.textureId = id;
        }

        public void renderTooltip(GuiGraphics poseStack, int mouseX, int mouseY) {
            WardrobeWidget.drawTooltipStatic(poseStack, Minecraft.getInstance().font, this.tooltip, mouseX + 3, mouseY);
        }

        private int getYOffset() {
            return this.parentWidget.selectedStyleButton == this ? 2 : isHovered ? 1 : 0;
        }
    }
}
