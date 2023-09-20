package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.joml.Quaternionf;

public class WardrobePreviewWidget extends ClickableWidget implements WardrobeWidget {
    private PlayerEntity previewPlayer;
    float previewRotation;

    public WardrobePreviewWidget(int x, int y, int width, int height, PlayerEntity player) {
        super(x, y, width, height, Text.literal("Player Preview"));
        this.previewPlayer = player;
    }

    @Override
    public void renderButton(DrawContext poseStack, int mouseX, int mouseY, float partialTick) {
        Window window = MinecraftClient.getInstance().getWindow();
        double scale = window.getScaleFactor();
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
        Window window = MinecraftClient.getInstance().getWindow();
        double guiScale = window.getScaleFactor();
        MatrixStack poseStack = RenderSystem.getModelViewStack();
        poseStack.push();
        poseStack.translate(getX() + (width / 2), getY() + height - (height / 6), 1050.0);
        poseStack.scale(1f, 1f, -1f);
        RenderSystem.applyModelViewMatrix();
        MatrixStack poseStack2 = new MatrixStack();
        poseStack2.translate(0.0, getY(), 1000.0);
        poseStack2.scale((float) ((window.getFramebufferHeight() / 3) / guiScale), (float) ((window.getFramebufferHeight() / 3) / guiScale), 1);
        Quaternionf quaternion = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternion2 = new Quaternionf().rotateY(previewRotation);
        quaternion.mul(quaternion2);
        poseStack2.multiply(quaternion);
        float h = this.previewPlayer.bodyYaw;
        float i = this.previewPlayer.getYaw();
        float j = this.previewPlayer.getPitch();
        float k = this.previewPlayer.prevHeadYaw;
        float l = this.previewPlayer.headYaw;
        this.previewPlayer.bodyYaw = 160f;
        this.previewPlayer.setYaw(160.0f);
        this.previewPlayer.setPitch(0f);
        this.previewPlayer.headYaw = this.previewPlayer.getYaw();
        this.previewPlayer.prevHeadYaw = this.previewPlayer.getYaw();
        DiffuseLighting.method_34742(); //Setup Entity Lighting
        EntityRenderDispatcher renderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        VertexConsumerProvider.Immediate bufferSource = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        quaternion2.conjugate();
        renderDispatcher.setRotation(quaternion2);
        renderDispatcher.setRenderShadows(false);
        RenderSystem.runAsFancy(() -> renderDispatcher.render(this.previewPlayer, 0, 0, 0, 0, 1f, poseStack2, bufferSource, 0xF000F0));
        bufferSource.draw();
        renderDispatcher.setRenderShadows(true);
        this.previewPlayer.bodyYaw = h;
        this.previewPlayer.setYaw(i);
        this.previewPlayer.setPitch(j);
        this.previewPlayer.prevHeadYaw = k;
        this.previewPlayer.headYaw = l;
        poseStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isValidClickButton(button) && this.clicked(mouseX, mouseY);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.previewRotation += (float) (dragX * 0.025f);
    }
}
