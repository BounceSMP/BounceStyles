package dev.bsmp.bouncestyles.client.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public interface WardrobeWidget extends Element, Drawable {
    default void drawTooltip(Text text, int x, int y, TextRenderer font, DrawContext context, int right) {
        drawTooltipStatic(text, x, y, font, context, right);
    }

    default void drawTooltipBackground(DrawContext context, int x, int y, int width, int height) {
        drawTooltipBackgroundStatic(context, x, y, width, height);
    }

    static void drawTooltipStatic(Text text, int x, int y, TextRenderer font, DrawContext context, int right) {
        if(right <= 0) right = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int textWidth = font.getWidth(text) + 3;
        int textX = x + 4 + textWidth > right ? x + (right - (x + textWidth)) - 2 : x + 2;

        MatrixStack poseStack = context.getMatrices();
        poseStack.push();
        GlStateManager._enableDepthTest();
        poseStack.translate(0, 0, 100);
        drawTooltipBackgroundStatic(context, textX, y - 13, textWidth + 1, 16);
        context.drawTextWithShadow(font, text, textX + 3, y - 9, 0xFFFFFF);
        poseStack.pop();
    }

    static void drawTooltipBackgroundStatic(DrawContext context, int x, int y, int width, int height) {
        context.fill(x, y + height - 1, x + width, y + 1, 0xFF000000);

        context.fill(x, y + height - 2, x + width, y + height - 1, 0xFF00A8A8);
        context.fill(x, y + 1, x + width, y + 2, 0xFF00A8A8);

        context.fill(x, y + height - 2, x + 1, y + 2, 0xFF00A8A8);
        context.fill(x + width, y + height - 2, x + width + 1, y + 2, 0xFF00A8A8);
    }
}
