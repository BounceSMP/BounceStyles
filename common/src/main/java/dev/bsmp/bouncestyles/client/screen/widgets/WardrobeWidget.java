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

    static void drawTooltipStatic(Text text, int x, int y, TextRenderer font, MatrixStack poseStack, int right) {
        if(right <= 0) right = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int textWidth = font.getWidth(text) + 3;
        int textX = x + 4 + textWidth > right ? x + (right - (x + textWidth)) - 2 : x + 2;

        poseStack.push();
        GlStateManager._enableDepthTest();
        poseStack.translate(0, 0, 100);

        fill(poseStack, textX, y - 11, x + textWidth + 3, y + 1, 0xFF000000);

        fill(poseStack, textX, y - 12, x + textWidth + 3, y - 11, 0xFF00A8A8);
        fill(poseStack, textX, y + 1, x + textWidth + 3, y + 2, 0xFF00A8A8);

        fill(poseStack, textX, y - 12, textX + 1, y + 2, 0xFF00A8A8);
        fill(poseStack, textX + textWidth + 1, y - 12, textX + textWidth + 2, y + 2, 0xFF00A8A8);

        drawTextWithShadow(poseStack, font, text, textX + 3, y - 9, 0xFFFFFF);
        poseStack.pop();
    }
}
