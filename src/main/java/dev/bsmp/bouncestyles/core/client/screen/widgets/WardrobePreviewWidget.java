package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class WardrobePreviewWidget extends AbstractWidget implements WardrobeWidget {
    private Player previewPlayer;
    float previewRotation;

    public WardrobePreviewWidget(int x, int y, int width, int height, Player player) {
        super(x, y, width, height, Component.literal("Player Preview"));
        this.previewPlayer = player;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float scale = getHeight() / 3f;

        //? if >= 1.21.11 {
        guiGraphics.pose().pushMatrix();
        //? } else {
//        guiGraphics.pose().pushPose();
//        guiGraphics.pose().translate(0, 0, 1050);
        //? }

        guiGraphics.enableScissor(this.getX() + 3, this.getY() + 3, this.getX() + getWidth() - 2, this.getY() + getHeight() - 3);

        float yBodyRot = previewPlayer.yBodyRot;
        float yRot = previewPlayer.getYRot();
        float xRot = previewPlayer.getXRot();
        float yHeadRot0 = previewPlayer.yHeadRotO;
        float yHeadRot = previewPlayer.yHeadRot;

        previewPlayer.yBodyRot = 180.0F;
        previewPlayer.setYRot(180.0F);
        previewPlayer.setXRot(0f);
        previewPlayer.yHeadRot = previewPlayer.getYRot();
        previewPlayer.yHeadRotO = previewPlayer.getYRot();

        //? if >= 1.21.11 {
        var pos1 = new Vector2i(this.getX() + 3, this.getY() + 3);
        var pos2 = new Vector2i(this.getX() + this.getWidth() - 3, this.getY() + this.getHeight() - 3);

        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.PI).rotateY((float) Math.toRadians(this.previewRotation));
        EntityRenderState entityRenderState = extractRenderState(previewPlayer);
//        if (entityRenderState instanceof LivingEntityRenderState livingEntityRenderState) {
//            livingEntityRenderState.bodyRot = 180.0F + h * 20.0F;
//            livingEntityRenderState.yRot = h * 20.0F;
//            if (livingEntityRenderState.pose != Pose.FALL_FLYING) {
//                livingEntityRenderState.xRot = -i * 20.0F;
//            } else {
//                livingEntityRenderState.xRot = 0.0F;
//            }
//
//            livingEntityRenderState.boundingBoxWidth = livingEntityRenderState.boundingBoxWidth / livingEntityRenderState.scale;
//            livingEntityRenderState.boundingBoxHeight = livingEntityRenderState.boundingBoxHeight / livingEntityRenderState.scale;
//            livingEntityRenderState.scale = 1.0F;
//        }

        guiGraphics.submitEntityRenderState(
                entityRenderState, scale,
                new Vector3f(0.0F, entityRenderState.boundingBoxHeight / 2.0F, 0.0F),
                rotation, null,
                pos1.x(), pos1.y(), pos2.x(), pos2.y()
        );
        //? } elif >= 1.21.1 {
        /*Vector3f translate = new Vector3f(0.0F,  (previewPlayer.getBbHeight() / 2.0F) + 0.1f, 0.0F);
        InventoryScreen.renderEntityInInventory(guiGraphics, getX() + (getWidth() / 2), getY() + (getHeight() / 2), scale / previewPlayer.getScale(), translate, new Quaternionf().rotateZ((float) Math.PI).rotateY(previewRotation), new Quaternionf(), previewPlayer);
        *///? } else {
        /*guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, (this.height / 3f), 0);
        InventoryScreen.renderEntityInInventory(guiGraphics, getX() + (getWidth() / 2), getY() + (getHeight() / 2), (int) (scale / previewPlayer.getScale()), new Quaternionf().rotateZ((float) Math.PI).rotateY(previewRotation), new Quaternionf(), previewPlayer);
        guiGraphics.pose().popPose();
        *///? }

        previewPlayer.yBodyRot = yBodyRot;
        previewPlayer.setYRot(yRot);
        previewPlayer.setXRot(xRot);
        previewPlayer.yHeadRotO = yHeadRot0;
        previewPlayer.yHeadRot = yHeadRot;

        guiGraphics.disableScissor();

        //? if >= 1.21.11 {
        guiGraphics.pose().pushMatrix();
        //? } else
//        guiGraphics.pose().popPose();
    }

    private static EntityRenderState extractRenderState(LivingEntity entity) {
        EntityRenderState entityRenderState = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity).createRenderState(entity, 1.0F);
        entityRenderState.lightCoords = 15728880;
        entityRenderState.shadowPieces.clear();
        entityRenderState.outlineColor = 0;
        return entityRenderState;
    }

    //? if >= 1.21.11 {
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return this.isValidClickButton(event.buttonInfo()) && this.isMouseOver(event.x(), event.y());
    }
    //? } else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isValidClickButton(button) && this.clicked(mouseX, mouseY);
    }
    *///? }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    //? if >= 1.21.11 {
    @Override
    protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
        this.previewRotation += (float) dragX * 0.9f;
    }
    //? } else {
    /*@Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.previewRotation += (float) (dragX / (getWidth() / 3f));
    }
    *///? }
}
