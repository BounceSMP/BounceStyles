package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import static net.minecraft.client.gui.DrawableHelper.drawTextWithShadow;
import static net.minecraft.client.gui.DrawableHelper.fill;

public interface WardrobeWidget extends Element, Drawable {
    default void drawTooltip(Text text, int x, int y, TextRenderer font, MatrixStack poseStack, int right) {
        drawTooltipStatic(text, x, y, font, poseStack, right);
    }

    default void drawTooltipBackground(MatrixStack poseStack, int x, int y, int width, int height) {
        drawTooltipBackgroundStatic(poseStack, x, y, width, height);
    }

    static void drawTooltipStatic(Text text, int x, int y, TextRenderer font, MatrixStack poseStack, int right) {
        if(right <= 0) right = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int textWidth = font.getWidth(text) + 3;
        int textX = x + 4 + textWidth > right ? x + (right - (x + textWidth)) - 2 : x + 2;

        poseStack.push();
        GlStateManager._enableDepthTest();
        poseStack.translate(0, 0, 100);
        drawTooltipBackgroundStatic(poseStack, textX, y - 13, textWidth + 1, 16);
        drawTextWithShadow(poseStack, font, text, textX + 3, y - 9, 0xFFFFFF);
        poseStack.pop();
    }

    static void drawTooltipBackgroundStatic(MatrixStack poseStack, int x, int y, int width, int height) {
        fill(poseStack, x, y + height - 1, x + width, y + 1, 0xFF000000);

        fill(poseStack, x, y + height - 2, x + width, y + height - 1, 0xFF00A8A8);
        fill(poseStack, x, y + 1, x + width, y + 2, 0xFF00A8A8);

        fill(poseStack, x, y + height - 2, x + 1, y + 2, 0xFF00A8A8);
        fill(poseStack, x + width, y + height - 2, x + width + 1, y + 2, 0xFF00A8A8);
    }
}
