package dev.bsmp.bouncestyles.core.client.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;

public interface WardrobeWidget extends GuiEventListener, Renderable {
    default void drawTooltip(Component text, int x, int y, Font font, GuiGraphics context, int right) {
        drawTooltipStatic(text, x, y, font, context, right);
    }

    default void drawTooltipBackground(GuiGraphics context, int x, int y, int width, int height) {
        drawTooltipBackgroundStatic(context, x, y, width, height);
    }

    static void drawTooltipStatic(Component text, int x, int y, Font font, GuiGraphics context, int right) {
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

        context.fill(x, y + height - 2, x + width, y + height - 1, 0xFF00A8A8);
        context.fill(x, y + 1, x + width, y + 2, 0xFF00A8A8);

        context.fill(x, y + height - 2, x + 1, y + 2, 0xFF00A8A8);
        context.fill(x + width, y + height - 2, x + width + 1, y + 2, 0xFF00A8A8);
    }
}
