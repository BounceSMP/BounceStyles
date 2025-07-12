package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
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
        Window window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
        float scale = (float) ((getHeight() / 3f));

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 1050);
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

        //? if <= 1.20.1 {
        /*guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, (this.height / 3f), 0);
        InventoryScreen.renderEntityInInventory(guiGraphics, getX() + (getWidth() / 2), getY() + (getHeight() / 2), (int) (scale / previewPlayer.getScale()), new Quaternionf().rotateZ((float) Math.PI).rotateY(previewRotation), new Quaternionf(), previewPlayer);
        guiGraphics.pose().popPose();
        *///?} else if >= 1.21.1 {
        Vector3f translate = new Vector3f(0.0F,  (previewPlayer.getBbHeight() / 2.0F) + 0.1f, 0.0F);
        InventoryScreen.renderEntityInInventory(guiGraphics, getX() + (getWidth() / 2), getY() + (getHeight() / 2), scale / previewPlayer.getScale(), translate, new Quaternionf().rotateZ((float) Math.PI).rotateY(previewRotation), new Quaternionf(), previewPlayer);
        //?}

        previewPlayer.yBodyRot = yBodyRot;
        previewPlayer.setYRot(yRot);
        previewPlayer.setXRot(xRot);
        previewPlayer.yHeadRotO = yHeadRot0;
        previewPlayer.yHeadRot = yHeadRot;

        guiGraphics.disableScissor();
        guiGraphics.pose().popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isValidClickButton(button) && this.clicked(mouseX, mouseY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.previewRotation += (float) (dragX / (getWidth() / 3f));
    }
}
