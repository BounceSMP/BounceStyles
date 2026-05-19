package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

//? if >= 1.21.11 {
//? } else {
//import com.mojang.blaze3d.platform.GlStateManager;
//? }

public abstract class WardrobeScrollWidget extends AbstractWidget {
    private static final Identifier TEX_WIDGETS = BounceStyles.id("textures/gui/widgets.png");

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
        this.previewRotation += 0.05f * partialTick;
        if (this.updateButtons)
            this.updateButtons();
        if (this.updateVisible)
            this.updateVisibleButtons();

        StyleButton tooltipButton = null;

        for (StyleButton button : this.visibleButtons) {
            //? if >= 1.21.11 {
            button.render(context, mouseX, mouseY, partialTick);
            //? } else {
//            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
//            button.renderWidget(context, bufferSource, mouseX, mouseY, partialTick);
            //? }
            if (button.isHovered())
                tooltipButton = button;
        }

        //? if < 1.21.11 {
        /*if (tooltipButton != null) {
            tooltipButton.renderTooltip(context, mouseX, mouseY);
        }
        *///? }

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

    protected abstract void onSelectionClicked(StyleButton button, int mouseButton);
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

    //? if <= 1.20.1 {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.scroll = Math.min(Math.max(this.scroll - (int) delta, 0), getTotalRows() - this.rows);
        this.updateVisible = true;
        return true;
    }
    *///?} else if >= 1.21.1 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scroll = Math.min(Math.max(this.scroll - (int) scrollY, 0), getTotalRows() - this.rows);
        this.updateVisible = true;
        return true;
    }
    //?}

    //? if >= 1.21.11 {
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        for (StyleButton button : this.visibleButtons)
            if (button.mouseClicked(event, doubleClick))
                return true;
        return false;
    }
    //? } else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (StyleButton button : this.visibleButtons)
            if(button.mouseClicked(mouseX, mouseY, mouseButton))
                return true;
        return false;
    }
    *///? }

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
        private boolean showVariants;

        public StyleButton(WardrobeScrollWidget parentWidget, int x, int y, int width, int height, Category category, Style style, boolean showVariants) {
            super(x, y, width, height, Component.empty(), null, DEFAULT_NARRATION);
            this.parentWidget = parentWidget;
            this.category = category;
            this.style = style;
            this.tooltip = createTooltip(style, category);
            this.showVariants = showVariants;
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

        //? if >= 1.21.11 {
        @Override
        protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//            this.isHovered = this.isMouseOver(mouseX, mouseY);
//
//            guiGraphics.blit(TEX_WIDGETS, this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 0, 0, 50, 50);
//            if (this.showVariants && this.style.hasVariants()) {
//                int x0 = this.getX() + (this.getWidth() / 2) - 5;
//                int y0 = this.getY() + this.getHeight() - 8;
//                guiGraphics.blit(TEX_WIDGETS, x0, y0, x0 + 10, y0 + 7, 50, 48, 59, 54);
//            }
//
//            if (!this.isHovered)
//                guiGraphics.enableScissor(this.getX() + 8, this.getY() + 8, this.getX() + this.getWidth() - 8, this.getY() + this.getHeight() - 8);
//
//            var poseStack = guiGraphics.pose();
//            poseStack.pushMatrix();
//            poseStack.translate(getX() + (this.width / 2), getY() + this.height);
//
//            if(isHovered()) {
//                poseStack.scale((float) (height * 0.7), (float) (height * 0.7));
//            }
//            else {
//                poseStack.scale((float) (height * 0.6), (float) (height * 0.6));
//            }
//
//            poseStack.rotate(this.parentWidget.previewRotation);
//
//            EntityRenderState renderState =
//            guiGraphics.submitGuiElementRenderState();
//
//            poseStack.popMatrix();
//
//            if (!this.isHovered)
//                guiGraphics.disableScissor();
        }
        //? } else {
        /*public void renderWidget(GuiGraphics context, MultiBufferSource.BufferSource bufferSource, int mouseX, int mouseY, float partialTick) {
            this.isHovered = this.isMouseOver(mouseX, mouseY);

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            context.blit(TEX_WIDGETS, this.getX(), this.getY(), this.width, this.height, 0, getYOffset() * 50,  50, 50, 256, 256);
            if (this.showVariants && this.style.hasVariants())
                context.blit(TEX_WIDGETS, this.getX() + (this.getWidth() / 2) - 5, this.getY() + this.getHeight() - 8, 50, 48 + (getYOffset() * 7),  10, 7);

            if (!this.isHovered)
                context.enableScissor(this.getX() + 8, this.getY() + 8, this.getX() + this.getWidth() - 8, this.getY() + this.getHeight() - 8);

            var poseStack = context.pose();
            poseStack.pushPose();
            poseStack.translate(getX() + (this.width / 2), getY() + this.height, 1000);
            poseStack.scale(1.0f, 1.0f, -1.0f);

            if(isHovered()) {
                poseStack.scale((float) (height * 0.7), (float) (height * 0.7), 10f);
            }
            else {
                poseStack.scale((float) (height * 0.6), (float) (height * 0.6), 10f);
            }

            Quaternionf quaternion = new Quaternionf().rotateZ((float) Math.PI);
            quaternion.rotateY(this.parentWidget.previewRotation);
            poseStack.mulPose(quaternion);

            //? if <= 1.20.1 {
            /^Lighting.setupLevel(context.pose().last().pose());
            ^///?} else if >= 1.21.1 {
            GlStateManager.setupLevelDiffuseLighting(new Vector3f(0.2F, 1.0F, -0.7F).normalize(), new Vector3f(-0.2F, 1.0F, 0.7F).normalize(), context.pose().last().pose());
            //?}

            RenderSystem.disableDepthTest();
            RenderSystem.runAsFancy(() -> {
                BounceStylesClient.STYLE_RENDERER.renderStyleForGUI(
                        poseStack,
                        this.style,
                        this.textureId,
                        this.category,
                        context.bufferSource(),
                        0f,
                        partialTick
                );
            });
            RenderSystem.enableDepthTest();

            context.flush();
            Lighting.setupFor3DItems();

            poseStack.popPose();

            if (!this.isHovered)
                context.disableScissor();
        }
        *///? }

        //? if >= 1.21.11 {
        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            if (this.active && this.visible && this.isMouseOver(event.x(), event.y())) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.parentWidget.onSelectionClicked(this, event.button());
                return true;
            }
            return false;
        }
        //? } else {
        /*@Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.active && this.visible && this.clicked(mouseX, mouseY)) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.parentWidget.onSelectionClicked(this, button);
                return true;
            }
            return false;
        }
        *///? }

        public Style getStyle() {
            return this.style;
        }

        public int getTextureId() {
            return this.textureId;
        }

        public void setTextureId(int id) {
            this.textureId = id;
        }

//        public void renderTooltip(GuiGraphics poseStack, int mouseX, int mouseY) {
//            poseStack.pose().pushPose();
//            poseStack.pose().translate(0, 0, 1050);
//            WardrobeWidget.drawTooltipStatic(poseStack, Minecraft.getInstance().font, this.tooltip, mouseX + 3, mouseY);
//            poseStack.pose().popPose();
//        }

        private int getYOffset() {
            return this.parentWidget.selectedStyleButton == this ? 2 : isHovered ? 1 : 0;
        }
    }
}
