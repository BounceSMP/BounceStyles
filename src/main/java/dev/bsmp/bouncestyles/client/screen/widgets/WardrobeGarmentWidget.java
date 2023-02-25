package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.GarmentLoader;
import dev.bsmp.bouncestyles.client.BounceStylesClient;
import dev.bsmp.bouncestyles.data.Garment;
import dev.bsmp.bouncestyles.data.PlayerStyleData;
import dev.bsmp.bouncestyles.networking.EquipStyleC2S;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.objectweb.asm.util.CheckAnnotationAdapter;

import java.util.ArrayList;
import java.util.List;

public class WardrobeGarmentWidget extends AbstractWidget {
    private static final ResourceLocation TEX_WIDGETS = new ResourceLocation(BounceStyles.modId, "textures/gui/widgets.png");

    List<GarmentButton> buttons = new ArrayList<>();
    GarmentButton selectedButton;
    GarmentLoader.Category category;

    float previewRotation = 0f;
    int buttonsPerRow = 6;
    int rowsPerPage = 4;
    int totalRows;
    int scroll = 0;
    int left;
    int top;
    int scaledXMargin;
    int scaledYMargin;

    public WardrobeGarmentWidget(int x, int y, int width, int height) {
        super(x, y, width, height, new TextComponent("Wardrobe Selection"));
        updateButtons(null, new ArrayList<>());
    }

    public void updateButtons(GarmentLoader.Category category, List<Garment> garments) {
        this.scroll = 0;
        this.category = category;
        this.buttons.clear();
        this.previewRotation = -30f;
        PlayerStyleData styleData = PlayerStyleData.getPlayerData(Minecraft.getInstance().player);

        Window window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
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

        this.totalRows = (garments.size() / this.buttonsPerRow) + 1;

        int heightOverflow = ((rowsPerPage * xMargin) + (rowsPerPage * btnSize)) - actualHeight;
        if(heightOverflow > 0) {
            btnSize -= heightOverflow;
            scaledBtnSize = (int) (btnSize / guiScale);
        }
        int remainingWidth = workingWidth - ((buttonsPerRow * btnSize) + (buttonsPerRow * xMargin));
        int remainingHeight = workingHeight - ((rowsPerPage * btnSize) + (rowsPerPage * yMargin));
//        if(remainingWidth > 0)
//            xMargin += remainingWidth / buttonsPerRow;
//        if(remainingHeight > 0)
//            yMargin += remainingHeight / rowsPerPage;
        this.scaledXMargin = (int) (xMargin / guiScale);
        this.scaledYMargin = (int) (yMargin / guiScale);

        this.left = x + this.scaledXMargin;
        this.top = y + this.scaledYMargin;

        int index = 0;
        for(int row = 0; row < this.totalRows; row++) {
            for(int i = 0; i < this.buttonsPerRow; i++) {
                if(index < garments.size()) {
                    Garment garment = garments.get(index);
                    GarmentButton button = new GarmentButton(
                            this, this.left + (i * this.scaledXMargin) + (i * scaledBtnSize), this.top + (row * this.scaledYMargin) + (row * scaledBtnSize),
                            scaledBtnSize, scaledBtnSize, garment);
                    this.buttons.add(button);
                    if(styleData != null && styleData.getGarmentForSlot(category) == garment)
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
                        GarmentButton button = this.buttons.get(index);
                        if(button.mouseClicked(mouseX, mouseY, mouseButton))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if(this.totalRows <= rowsPerPage)
            return false;
        if(this.scroll - delta < 0)
            return false;
        if(this.scroll - delta > 3)
            return false;
        this.scroll -= delta;
        return true;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.previewRotation = this.previewRotation >= 360 ? 0 : this.previewRotation + (partialTick * 2.5f);
        for(int row = scroll; row < scroll + rowsPerPage; row++) {
            for(int i = 0; i < this.buttonsPerRow; i++) {
                int index = (row * this.buttonsPerRow) + i;
                if(index < this.buttons.size()) {
                    GarmentButton button = this.buttons.get(index);
                    button.y = top + ((row - scroll) * button.getHeight()) + ((row - scroll) * scaledYMargin);
                    button.render(poseStack, mouseX, mouseY, partialTick);
                }
            }
        }
        if(isHovered) {
            for (int row = scroll; row < scroll + rowsPerPage; row++) {
                for (int i = 0; i < this.buttonsPerRow; i++) {
                    int index = (row * this.buttonsPerRow) + i;
                    if (index < this.buttons.size()) {
                        GarmentButton button = this.buttons.get(index);
                        if (button.isHoveredOrFocused()) {
                            button.renderToolTip(poseStack, mouseX, mouseY);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static class GarmentButton extends Button {
        private WardrobeGarmentWidget parentWidget;
        private Garment garment;

        public GarmentButton(WardrobeGarmentWidget parentWidget, int x, int y, int width, int height, Garment garment) {
            super(x, y, width, height, new TextComponent(garment.garmentId.toString()), null);
            this.parentWidget = parentWidget;
            this.garment = garment;
        }

        @Override
        public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTick) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEX_WIDGETS);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);

            blit(stack, this.x, this.y, this.width, this.height, 0, getYOffset() * 50, 50, 50, 256, 256);

            float guiScale = (float) Minecraft.getInstance().getWindow().getGuiScale();

            PoseStack poseStack = RenderSystem.getModelViewStack();
            poseStack.pushPose();
            poseStack.translate(this.x + (this.width / 2), this.y + this.height, 1050.0);
            poseStack.scale(1.0f, 1.0f, -1.0f);
            RenderSystem.applyModelViewMatrix();
            PoseStack poseStack2 = new PoseStack();
            float y = switch (parentWidget.category) {
                case Head -> -(float)(.5f);
                case Body -> -(1f);
                case Legs, Feet -> -1.8F;
            };
            poseStack2.translate(0.0, 0, 1000.0);
            poseStack2.scale((float) (height * 0.7), (float) (height * 0.7), 10f);
            poseStack2.translate(0.0, y, 0.0);
            Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0f);
            Quaternion quaternion2 = Vector3f.YP.rotationDegrees(parentWidget.previewRotation);
            quaternion.mul(quaternion2);
            poseStack2.mulPose(quaternion);
            Lighting.setupForEntityInInventory();
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            RenderSystem.runAsFancy(() -> BounceStylesClient.GARMENT_RENDERER.renderGarmentForGUI(
                    poseStack2,
                    this.garment,
                    this.parentWidget.category,
                    bufferSource,
                    partialTick,
                    0xF000F0
            ));
            bufferSource.endBatch();
            poseStack.popPose();
            RenderSystem.applyModelViewMatrix();
            Lighting.setupFor3DItems();
        }

        @Override
        public void onPress() {
            if(this.parentWidget.selectedButton == this) {
                EquipStyleC2S.sendToServer(this.parentWidget.category, null);
                this.parentWidget.selectedButton = null;
            }
            else {
                EquipStyleC2S.sendToServer(this.parentWidget.category, this.garment);
                this.parentWidget.selectedButton = this;
            }
        }

        @Override
        public void renderToolTip(PoseStack poseStack, int mouseX, int mouseY) {
            Font font = Minecraft.getInstance().font;
            int textWidth = font.width(getMessage()) + 1;
            int right = parentWidget.x + parentWidget.width;
            int textX = mouseX + 2 + textWidth > right ? mouseX + (right - (mouseX + textWidth)) - 2 : mouseX + 2;
            poseStack.translate(0, 0, 100);
            fill(poseStack, textX, mouseY - 11, mouseX + textWidth + 3, mouseY + 1, 0xFF000000);
            hLine(poseStack, textX, mouseX + textWidth + 2, mouseY - 12, 0xFF00A8A8);
            hLine(poseStack, textX, mouseX + textWidth + 2, mouseY + 1, 0xFF00A8A8);
            vLine(poseStack, textX, mouseY - 12, mouseY + 2, 0xFF00A8A8);
            vLine(poseStack, textX + textWidth + 1, mouseY - 13, mouseY + 2, 0xFF00A8A8);
            drawString(poseStack, font, getMessage(), textX + 2, mouseY - 9, 0xFFFFFF);
        }

        private int getYOffset() {
            return this.parentWidget.selectedButton == this ? 2 : isHovered ? 1 : 0;
        }

        @Override
        public void updateNarration(NarrationElementOutput narrationElementOutput) {}
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}

}
