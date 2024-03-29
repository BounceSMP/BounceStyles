package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleRegistry;
import dev.bsmp.bouncestyles.client.BounceStylesClient;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.networking.serverbound.EquipStyleServerbound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.builder.Diff;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class WardrobeStyleWidget extends ClickableWidget implements WardrobeWidget {
    private static final Identifier TEX_WIDGETS = new Identifier(BounceStyles.modId, "textures/gui/widgets.png");

    List<StyleButton> buttons = new ArrayList<>();
    StyleButton selectedButton;
    StyleRegistry.Category category;

    float previewRotation = 0f;
    int buttonsPerRow = 6;
    int rowsPerPage = 4;
    int totalRows;
    int scroll = 0;
    int left;
    int top;
    int scaledXMargin;
    int scaledYMargin;

    public WardrobeStyleWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Text.literal("Wardrobe Selection"));
        updateButtons(null, new ArrayList<Style>());
    }

    public void updateButtons(StyleRegistry.Category category, List<Style> styles) {
        this.scroll = 0;
        this.category = category;
        this.buttons.clear();
        this.previewRotation = -30f;
        StyleData styleData = StyleData.getOrCreateStyleData(MinecraftClient.getInstance().player);

        Window window = MinecraftClient.getInstance().getWindow();
        double guiScale = window.getScaleFactor();
        int actualWidth = (int) (width * guiScale);
        int actualHeight = (int) (height * guiScale);

        int xMarginCount = this.buttonsPerRow + 1;
        int yMarginCount = this.rowsPerPage + 1;
        int xMargin = 10;
        int yMargin = 10;

        int workingWidth = actualWidth - (xMarginCount * xMargin);
        int workingHeight = actualHeight - (yMarginCount * yMargin);

        int btnSize = workingWidth / this.buttonsPerRow;
        int scaledBtnSize = (int) (btnSize / guiScale);

        this.totalRows = (styles.size() / this.buttonsPerRow) + 1;

        int heightOverflow = ((rowsPerPage * xMargin) + (rowsPerPage * btnSize)) - actualHeight;
        if(heightOverflow > 0) {
            btnSize -= heightOverflow;
            scaledBtnSize = (int) (btnSize / guiScale);
        }
        this.scaledXMargin = (int) (xMargin / guiScale);
        this.scaledYMargin = (int) (yMargin / guiScale);

        this.left = getX() + this.scaledXMargin;
        this.top = getY() + this.scaledYMargin;

        int index = 0;
        for(int row = 0; row < this.totalRows; row++) {
            for(int i = 0; i < this.buttonsPerRow; i++) {
                if(index < styles.size()) {
                    Style style = styles.get(index);
                    StyleButton button = new StyleButton(
                            this, this.left + (i * this.scaledXMargin) + (i * scaledBtnSize), this.top + (row * this.scaledYMargin) + (row * scaledBtnSize),
                            scaledBtnSize, scaledBtnSize, style);
                    this.buttons.add(button);
                    if(styleData != null && styleData.getStyleForSlot(category) == style)
                        this.selectedButton = button;
                }
                index++;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!this.active || !this.visible) {
            return false;
        }
        if (this.isValidClickButton(mouseButton) && this.clicked(mouseX, mouseY)) {
            for(int row = scroll; row < scroll + rowsPerPage; row++) {
                for(int i = 0; i < this.buttonsPerRow; i++) {
                    int index = (row * this.buttonsPerRow) + i;
                    if(index < this.buttons.size()) {
                        StyleButton button = this.buttons.get(index);
                        if(button.mouseClicked(mouseX, mouseY, mouseButton))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if(this.totalRows <= rowsPerPage)
            return false;
        if(this.scroll - delta < 0)
            return false;
        if(this.scroll - delta > (totalRows - rowsPerPage))
            return false;
        this.scroll -= delta;
        return true;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float partialTick) {
        this.previewRotation = this.previewRotation + (partialTick * 0.05f);

        for(int row = scroll; row < scroll + rowsPerPage; row++) {
            for(int i = 0; i < this.buttonsPerRow; i++) {
                int index = (row * this.buttonsPerRow) + i;
                if(index < this.buttons.size()) {
                    StyleButton button = this.buttons.get(index);
                    button.setY(top + ((row - scroll) * button.getHeight()) + ((row - scroll) * scaledYMargin));
                    button.render(context, mouseX, mouseY, partialTick);
                }
            }
        }

        if(hovered) {
            for (int row = scroll; row < scroll + rowsPerPage; row++) {
                for (int i = 0; i < this.buttonsPerRow; i++) {
                    int index = (row * this.buttonsPerRow) + i;
                    if (index < this.buttons.size()) {
                        StyleButton button = this.buttons.get(index);
                        if (button.isHovered()) {
                            button.renderTooltip(context, mouseX, mouseY);
                            break;
                        }
                    }
                }
            }
        }

        if(totalRows > rowsPerPage) {
            int totalScrolls = (totalRows - rowsPerPage) + 1;
            int scrollHeight = totalScrolls * 10;
            int scrollTop = (getY() + (height / 2)) - (scrollHeight / 2);
            for(int i = 0; i < totalScrolls; i++) {
                int colour = i == scroll ? 0xFF00cccc : 0xFF222222;
                context.fill(getX() + width - 6, scrollTop + (i * 10), getX() + width - 1, scrollTop + (i * 10) + 5, colour);
            }
        }
    }

    public class StyleButton extends ButtonWidget {
        private WardrobeStyleWidget parentWidget;
        private Style style;

        public StyleButton(WardrobeStyleWidget parentWidget, int x, int y, int width, int height, Style style) {
            super(x, y, width, height, Text.translatable(style.styleId.getNamespace()+"."+style.styleId.getPath()+"."+parentWidget.category.name().toLowerCase()), null, DEFAULT_NARRATION_SUPPLIER);
            this.parentWidget = parentWidget;
            this.style = style;
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float partialTick) {
            Window window = MinecraftClient.getInstance().getWindow();
            double guiScale = window.getScaleFactor();

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
            context.drawTexture(TEX_WIDGETS, this.getX(), this.getY(), this.width, this.height, 0, getYOffset() * 50,  50, 50, 256, 256);

            if(!isHovered()) {
                int sOffset = width / 6;
                int sSize = sOffset * 2;
                RenderSystem.enableScissor(
                        (int) ((getX() + sOffset) * guiScale),
                        (int) ((window.getScaledHeight() - getY() - height + sOffset) * guiScale),
                        (int) ((width - sSize) * guiScale),
                        (int) (((height - sSize) * guiScale))
                );
            }

            MatrixStack poseStack = RenderSystem.getModelViewStack();
            poseStack.push();
            poseStack.translate(getX() + (this.width / 2), getY() + this.height, 1050.0);
            poseStack.scale(1.0f, 1.0f, -1.0f);
            RenderSystem.applyModelViewMatrix();
            MatrixStack poseStack2 = new MatrixStack();
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
            poseStack2.multiply(quaternion);
            DiffuseLighting.method_34742(); //Setup Entity Lighting
            VertexConsumerProvider.Immediate bufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            RenderSystem.runAsFancy(() -> BounceStylesClient.STYLE_RENDERER.renderStyle(
                    poseStack2,
                    this.style,
                    this.parentWidget.category,
                    bufferSource,
                    0f,
                    partialTick,
                    0xF000F0,
                    true
            ));
            bufferSource.draw();
            poseStack.pop();
            RenderSystem.applyModelViewMatrix();
            DiffuseLighting.enableGuiDepthLighting();

            if(!isHovered())
                RenderSystem.disableScissor();
        }

        @Override
        public void onPress() {
            if(this.parentWidget.selectedButton == this) {
                new EquipStyleServerbound(this.parentWidget.category, null).sendToServer();
                this.parentWidget.selectedButton = null;
            }
            else {
                new EquipStyleServerbound(this.parentWidget.category, this.style).sendToServer();
                this.parentWidget.selectedButton = this;
            }
        }

        public void renderTooltip(DrawContext poseStack, int mouseX, int mouseY) {
            drawTooltip(getMessage(), mouseX, mouseY, MinecraftClient.getInstance().textRenderer, poseStack, parentWidget.getX() + parentWidget.width);
        }

        private int getYOffset() {
            return this.parentWidget.selectedButton == this ? 2 : hovered ? 1 : 0;
        }
    }
}
