package dev.bsmp.bouncestyles.core.client.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.List;

//? if < 1.21.11
//import com.mojang.blaze3d.platform.GlStateManager;

public interface WardrobeWidget extends GuiEventListener, Renderable {
    //? if < 1.21.11 {
    /*default void drawTooltip(GuiGraphics context, Font font, Component text, int x, int y) {
        drawTooltipStatic(context, font, List.of(text), x, y);
    }

    default void drawTooltipBackground(GuiGraphics context, int x, int y, int width, int height) {
        drawTooltipBackgroundStatic(context, x, y, width, height);
    }

    static void drawTooltipStatic(GuiGraphics context, Font font, List<Component> text, int x, int y) {
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

        var poseStack = context.pose();
        poseStack.pushPose();
        GlStateManager._enableDepthTest();
        poseStack.translate(0, 0, 100);
        drawTooltipBackgroundStatic(context, textX, textY - 4, width + 4, 16 + ((text.size() - 1) * 9));

        int i = 0;
        for (Component line : text)
            context.drawString(font, line, textX + 3, textY + (i++ * 9), 0xFFFFFF);

        poseStack.popPose();
    }

    static void drawTooltipBackgroundStatic(GuiGraphics context, int x, int y, int width, int height) {
        context.fill(x, y + height - 1, x + width, y + 1, 0xFF000000);

        context.fill(x, y + height - 2, x + width + 1, y + height - 1, 0xFF00A8A8);
        context.fill(x, y + 1, x + width + 1, y + 2, 0xFF00A8A8);

        context.fill(x, y + height - 2, x + 1, y + 2, 0xFF00A8A8);
        context.fill(x + width, y + height - 2, x + width + 1, y + 2, 0xFF00A8A8);
    }
    *///? }
}
