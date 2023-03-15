package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class WardrobePreviewWidget extends ClickableWidget {

    private PlayerEntity previewPlayer;
    float previewRotation;

    public WardrobePreviewWidget(int x, int y, int width, int height, PlayerEntity player) {
        super(x, y, width, height, new LiteralText("Player Preview"));
        this.previewPlayer = player;
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTick) {
        Window window = MinecraftClient.getInstance().getWindow();
        double scale = window.getScaleFactor();
        RenderSystem.enableScissor(
                (int) ((x + 3) * scale),
                (int) (y * scale),
                (int) ((width - 5) * scale),
                (int) (height * scale)
        );
        renderPlayer();
        RenderSystem.disableScissor();
    }

    private void renderPlayer() {
        Window window = MinecraftClient.getInstance().getWindow();
        double guiScale = window.getScaleFactor();
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x + (width / 2), y + height - (height / 6), 1050.0F);
        RenderSystem.scalef(1f, 1f, -1f);
        MatrixStack poseStack2 = new MatrixStack();
        poseStack2.translate(0.0, y, 1000.0);
        poseStack2.scale((float) ((window.getFramebufferHeight() / 3) / guiScale), (float) ((window.getFramebufferHeight() / 3) / guiScale), 1);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180F);
        Quaternion quaternion2 = Vec3f.POSITIVE_Y.getDegreesQuaternion(previewRotation);
        quaternion.hamiltonProduct(quaternion2);
        poseStack2.multiply(quaternion);
        float h = this.previewPlayer.bodyYaw;
        float i = this.previewPlayer.yaw;
        float j = this.previewPlayer.pitch;
        float k = this.previewPlayer.prevHeadYaw;
        float l = this.previewPlayer.headYaw;
        this.previewPlayer.bodyYaw = 160f;
        this.previewPlayer.yaw = 160.0f;
        this.previewPlayer.pitch = 0f;
        this.previewPlayer.headYaw = this.previewPlayer.yaw;
        this.previewPlayer.prevHeadYaw = this.previewPlayer.yaw;
        EntityRenderDispatcher renderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        VertexConsumerProvider.Immediate bufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        quaternion2.conjugate();
        renderDispatcher.setRotation(quaternion2);
        renderDispatcher.setRenderShadows(false);
        RenderSystem.runAsFancy(() -> renderDispatcher.render(this.previewPlayer, 0, 0, 0, 0, 1f, poseStack2, bufferSource, 0xF000F0));
        bufferSource.draw();
        renderDispatcher.setRenderShadows(true);
        this.previewPlayer.bodyYaw = h;
        this.previewPlayer.yaw = i;
        this.previewPlayer.pitch = j;
        this.previewPlayer.prevHeadYaw = k;
        this.previewPlayer.headYaw = l;
        RenderSystem.popMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isValidClickButton(button) && this.clicked(mouseX, mouseY);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.previewRotation += (float) (dragX * 0.5f);
    }
}
