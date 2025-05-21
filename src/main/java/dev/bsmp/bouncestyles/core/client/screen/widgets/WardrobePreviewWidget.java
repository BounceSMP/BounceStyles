package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;

public class WardrobePreviewWidget extends AbstractWidget implements WardrobeWidget {
    private Player previewPlayer;
    float previewRotation;

    public WardrobePreviewWidget(int x, int y, int width, int height, Player player) {
        super(x, y, width, height, Component.literal("Player Preview"));
        this.previewPlayer = player;
    }

    @Override
    public void renderWidget(GuiGraphics poseStack, int mouseX, int mouseY, float partialTick) {
        Window window = Minecraft.getInstance().getWindow();
        double scale = window.getGuiScale();
        RenderSystem.enableScissor(
                (int) ((getX() + 3) * scale),
                (int) (getY() * scale),
                (int) ((width - 5) * scale),
                (int) (height * scale)
        );
        renderPlayer();
        RenderSystem.disableScissor();
    }

    private void renderPlayer() {
        Window window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        poseStack.translate(getX() + (width / 2), getY() + height - (height / 6), 1050.0);
        poseStack.scale(1f, 1f, -1f);
        RenderSystem.applyModelViewMatrix();
        PoseStack poseStack2 = new PoseStack();
        poseStack2.translate(0.0, getY(), 1000.0);
        poseStack2.scale((float) ((window.getHeight() / 3) / guiScale), (float) ((window.getHeight() / 3) / guiScale), 1);
        Quaternionf quaternion = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternion2 = new Quaternionf().rotateY(previewRotation);
        quaternion.mul(quaternion2);
        poseStack2.mulPose(quaternion);
        float h = this.previewPlayer.yBodyRot;
        float i = this.previewPlayer.getYRot();
        float j = this.previewPlayer.getXRot();
        float k = this.previewPlayer.yHeadRotO;
        float l = this.previewPlayer.yHeadRot;
        this.previewPlayer.yBodyRot = 160f;
        this.previewPlayer.setYRot(160.0f);
        this.previewPlayer.setXRot(0f);
        this.previewPlayer.yHeadRot = this.previewPlayer.getYRot();
        this.previewPlayer.yHeadRotO = this.previewPlayer.getYRot();
        Lighting.setupForEntityInInventory(); //Setup Entity Lighting
        EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        quaternion2.conjugate();
        renderDispatcher.overrideCameraOrientation(quaternion2);
        renderDispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> renderDispatcher.render(this.previewPlayer, 0, 0, 0, 0, 1f, poseStack2, bufferSource, 0xF000F0));
        bufferSource.endBatch();
        renderDispatcher.setRenderShadow(true);
        this.previewPlayer.yBodyRot = h;
        this.previewPlayer.setYRot(i);
        this.previewPlayer.setXRot(j);
        this.previewPlayer.yHeadRotO = k;
        this.previewPlayer.yHeadRot = l;
        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isValidClickButton(button) && this.clicked(mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.previewRotation += (float) (dragX * 0.025f);
    }
}
