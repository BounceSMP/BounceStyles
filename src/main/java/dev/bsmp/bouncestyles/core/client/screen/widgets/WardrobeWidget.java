package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface WardrobeWidget extends GuiEventListener, Renderable {
    default void drawTooltip(GuiGraphics context, Font font, Component text, int x, int y, int right) {
        drawTooltipStatic(context, font, text, x, y, right);
    }

    default void drawTooltipBackground(GuiGraphics context, int x, int y, int width, int height) {
        drawTooltipBackgroundStatic(context, x, y, width, height);
    }

    static void drawTooltipStatic(GuiGraphics context, Font font, List<Component> text, int x, int y, int right) {
        int width = 0;
        for (Component line : text) {
            int i = font.width(line);
            if (i > width) width = i;
        }

        int maxRight = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int textY = y - text.size() * 10 - 2;
        int textX = x + 2;
        if (textX + width + 6 > maxRight)
            textX = maxRight - width - 6;

        PoseStack poseStack = context.pose();
        poseStack.pushPose();
        GlStateManager._enableDepthTest();
        poseStack.translate(0, 0, 100);
        drawTooltipBackgroundStatic(context, textX, textY - 4, width + 4, 16 + ((text.size() - 1) * 9));

        int i = 0;
        for (Component line : text)
            context.drawString(font, line, textX + 3, textY + (i++ * 9), 0xFFFFFF);

        poseStack.popPose();
    }

    static void drawTooltipStatic(GuiGraphics context, Font font, Component text, int x, int y, int right) {
        if(right <= 0) right = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int textWidth = font.width(text) + 3;
        int textX = x + 4 + textWidth > right ? x + (right - (x + textWidth)) - 2 : x + 2;

        PoseStack poseStack = context.pose();
        poseStack.pushPose();
        GlStateManager._enableDepthTest();
        poseStack.translate(0, 0, 100);
        drawTooltipBackgroundStatic(context, textX, y - 13, textWidth + 1, 16);
        context.drawString(font, text, textX + 3, y - 9, 0xFFFFFF);
        poseStack.popPose();
    }

    static void drawTooltipBackgroundStatic(GuiGraphics context, int x, int y, int width, int height) {
        context.fill(x, y + height - 1, x + width, y + 1, 0xFF000000);

        context.fill(x, y + height - 2, x + width + 1, y + height - 1, 0xFF00A8A8);
        context.fill(x, y + 1, x + width + 1, y + 2, 0xFF00A8A8);

        context.fill(x, y + height - 2, x + 1, y + 2, 0xFF00A8A8);
        context.fill(x + width, y + height - 2, x + width + 1, y + 2, 0xFF00A8A8);
    }
}
