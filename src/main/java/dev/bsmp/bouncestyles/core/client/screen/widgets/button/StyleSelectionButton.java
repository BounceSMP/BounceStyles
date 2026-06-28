package dev.bsmp.bouncestyles.core.client.screen.widgets.button;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import dev.bsmp.bouncestyles.core.client.screen.widgets.WardrobeScrollWidget;
import dev.bsmp.bouncestyles.core.client.screen.widgets.WardrobeStyleSelectionWidget;
import dev.bsmp.bouncestyles.core.client.screen.widgets.WardrobeWidget;
import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.api.data.EquippedStyle;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.api.style.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

//? if >= 1.21.5 {
import net.minecraft.client.input.MouseButtonEvent;
import dev.bsmp.bouncestyles.core.client.renderer.StyleDataTickets;
import dev.bsmp.bouncestyles.core.client.renderer.StyleGuiRenderer;
import org.joml.Quaternionf;
//? } else {
/*import com.mojang.blaze3d.systems.RenderSystem;
*///? }

public class StyleSelectionButton extends Button implements WardrobeWidget {
    private static final Identifier TEX_BASE = BounceStyles.id("textures/gui/sprites/style_selection/style_selection.png");
    private static final Identifier TEX_HOVER = BounceStyles.id("textures/gui/sprites/style_selection/style_selection_hover.png");
    private static final Identifier TEX_EQUIPPED = BounceStyles.id("textures/gui/sprites/style_selection/style_selection_equipped.png");

    private static final Identifier TEX_VARIANT = BounceStyles.id("textures/gui/sprites/style_selection/style_selection_variant.png");
    private static final Identifier TEX_VARIANT_HOVER = BounceStyles.id("textures/gui/sprites/style_selection/style_selection_variant_hover.png");
    private static final Identifier TEX_VARIANT_EQUIPPED = BounceStyles.id("textures/gui/sprites/style_selection/style_selection_variant_equipped.png");

    private final WardrobeScrollWidget parentWidget;
    private final List<Component> tooltip;
    private final Category category;
    private final EquippedStyle style;

    public StyleSelectionButton(WardrobeScrollWidget parentWidget, int x, int y, int width, int height, Category category, Style style) {
        super(x, y, width, height, Component.empty(), null, DEFAULT_NARRATION);
        this.parentWidget = parentWidget;
        this.category = category;
        this.style = new EquippedStyle(style);
        this.tooltip = createTooltip(style, category);
    }

    private static List<Component> createTooltip(Style style, Category category) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable(style.getStyleId().getNamespace()+"."+style.getStyleId().getPath()+"."+category.name().toLowerCase()).withStyle(ChatFormatting.BOLD));
        style.getCredits().ifPresent(credits -> {
            StringJoiner joiner = new StringJoiner(", ");
            credits.forEach(joiner::add);
            list.add(Component.literal("Made By: " + joiner).withStyle(ChatFormatting.GRAY));
        });
        return list;
    }

        //? if >= 1.21.5 {
        @Override
        protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            renderBtn(guiGraphics, mouseX, mouseY, partialTick);
        }
        //? } else {
        /*public void renderWidget(GuiGraphics guiGraphics, MultiBufferSource.BufferSource bufferSource, int mouseX, int mouseY, float partialTick) {
            renderBtn(guiGraphics, mouseX, mouseY, partialTick);
        }
        *///? }

        public void renderBtn(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            this.isHovered = this.isMouseOver(mouseX, mouseY);
            var currentStyle = StyleData.getEntityData(Minecraft.getInstance().player).getStyleForSlot(this.category);
            boolean equipped = currentStyle.getStyleId().map(identifier -> identifier.equals(this.getStyle().getStyleId())).orElse(false);
            if (equipped && this.parentWidget instanceof WardrobeStyleSelectionWidget.SelectionPopup)
                equipped = this.getTextureId() == currentStyle.getVariant();

            var baseTexture = equipped ? TEX_EQUIPPED : this.isHovered() ? TEX_HOVER : TEX_BASE;
            blit(guiGraphics, baseTexture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 50, 50);

            if (!(this.parentWidget instanceof WardrobeStyleSelectionWidget.SelectionPopup) && this.getStyle().hasVariants()) {
                var variantTexture = equipped ? TEX_VARIANT_EQUIPPED : this.isHovered() ? TEX_VARIANT_HOVER : TEX_VARIANT;
                blit(guiGraphics, variantTexture, this.getX() + (this.getWidth() / 2) - 5, this.getY() + this.getHeight() - 6, 10, 7);
            }

            if (this.isHovered())
                //~ if >= 1.21.5 'renderComponentTooltip' -> 'setComponentTooltipForNextFrame'
                guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, this.tooltip, mouseX, mouseY);

            if (!this.isHovered)
                guiGraphics.enableScissor(this.getX() + 8, this.getY() + 8, this.getX() + this.getWidth() - 8, this.getY() + this.getHeight() - 8);

            renderStyle(new PoseStack(), guiGraphics, partialTick);

            if (!this.isHovered)
                guiGraphics.disableScissor();
        }

        private void renderStyle(PoseStack poseStack, GuiGraphics guiGraphics, float partialTick) {
            var translate = switch (this.category) {
                case Head -> new Vector3f(0f, -0.4f, 0f);
                case Body -> new Vector3f(0f, -1.1f, 0f);
                case Legs -> new Vector3f(0f, -1.9f, 0f);
                case Feet -> new Vector3f(0f, -2.0f, 0f);
            };

            //? if >= 1.21.5 {
            var renderState = BounceStylesClient.getStyleRenderer().createRenderState(null, null);
            renderState.addGeckolibData(StyleDataTickets.TICKET_EQUIPPED, this.style);
            renderState.addGeckolibData(StyleDataTickets.TICKET_CATEGORY, this.category);

            guiGraphics.guiRenderState.submitPicturesInPictureState(new StyleGuiRenderer.StyleGuiRenderState(
                    renderState,
                    translate,
                    new Quaternionf().rotationY(this.parentWidget.previewRotation),
                    this.getX(), this.getY(),
                    this.getX() + this.getWidth(), this.getY() + this.getHeight(),
                    30f,
                    isHovered,
                    guiGraphics.scissorStack.peek()
            ));
            //? } else {
            /*RenderSystem.runAsFancy(() -> BounceStylesClient.getStyleRenderer().renderStyleForGUI(
                    poseStack,
                    this.style.getStyle().get(),
                    this.style.getVariant(),
                    this.category,
                    guiGraphics.bufferSource(),
                    0f,
                    partialTick
            ));
            *///? }
        }

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
        return this.style.getStyle().get();
    }

    public int getTextureId() {
        return this.style.getVariant();
    }

    public void setTextureId(int id) {
        this.style.setVariant(id);
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
