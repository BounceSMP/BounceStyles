package dev.bsmp.bouncestyles.core.client.screen.widgets.button;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.screen.widgets.WardrobeScrollWidget;
import dev.bsmp.bouncestyles.core.client.screen.widgets.WardrobeWidget;
import dev.bsmp.bouncestyles.core.data.Category;
import dev.bsmp.bouncestyles.core.data.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class StyleSelectionButton extends Button implements WardrobeWidget {
    private static final Identifier TEX_BASE = BounceStyles.id("textures/gui/sprites/style_selection/style_selection.png");
    private static final Identifier TEX_HOVER = BounceStyles.id("textures/gui/sprites/style_selection/style_selection_hover.png");
    private static final Identifier TEX_EQUIPPED = BounceStyles.id("textures/gui/sprites/style_selection/style_selection_equipped.png");

    private static final Identifier TEX_VARIANT = BounceStyles.id("textures/gui/sprites/style_selection/style_selection_variant.png");
    private static final Identifier TEX_VARIANT_HOVER = BounceStyles.id("textures/gui/sprites/style_selection/style_selection_variant_hover.png");
    private static final Identifier TEX_VARIANT_EQUIPPED = BounceStyles.id("textures/gui/sprites/style_selection/style_selection_variant_equipped.png");

    private WardrobeScrollWidget parentWidget;
    private List<Component> tooltip;
    private Category category;
    private Style style;
    private int textureId = -1;
    private boolean showVariants;

    public StyleSelectionButton(WardrobeScrollWidget parentWidget, int x, int y, int width, int height, Category category, Style style, boolean showVariants) {
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
            StringJoiner joiner = new StringJoiner(", ");
            credits.forEach(joiner::add);
            list.add(Component.literal("Made By: " + joiner).withStyle(ChatFormatting.GRAY));
        });
        return list;
    }

    //? if >= 1.21.11 {
    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.isHovered = this.isMouseOver(mouseX, mouseY);

        var baseTexture = this.isHovered() ? TEX_HOVER : TEX_BASE;
        blit(guiGraphics, baseTexture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 50, 50);

        if (this.style.hasVariants()) {
            var variantTexture = this.isHovered() ? TEX_VARIANT_HOVER : TEX_VARIANT;
            blit(guiGraphics, variantTexture, this.getX() + (this.getWidth() / 2) - 5, this.getY() + this.getHeight() - 6, 10, 7);
        }

        if (this.isHovered())
            guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, this.tooltip, mouseX, mouseY);

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

//    private int getYOffset() {
//        return this.parentWidget.selectedStyleButton == this ? 2 : isHovered ? 1 : 0;
//    }
}
