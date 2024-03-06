package dev.bsmp.bouncestyles.client.screen;

import dev.bsmp.bouncestyles.client.BounceStylesClient;
import java.io.File;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class MissingWarningScreen extends Screen {
    private final int windowWidth = 340;
    private final int windowHeight = 150;
    private int left, top;

    private Checkbox dontShowAgain;
    private final File logFile;
    private final int hash;

    public MissingWarningScreen(File logFile, int hash) {
        super(Component.literal("Warning"));
        this.logFile = logFile;
        this.hash = hash;
    }

    @Override
    protected void init() {
        super.init();
        this.left = (width / 2) - (windowWidth / 2);
        this.top = (height / 2) - (windowHeight / 2);

        Component checkboxLabel = Component.translatable("gui.bounce_styles.missing.dont_show_again");
        dontShowAgain = addRenderableWidget(new Checkbox((width / 2) - 10 - (font.width(checkboxLabel) / 2), top + windowHeight - 25, 20, 20, checkboxLabel, false));
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        int i = 1;
        int j = 16;
        context.drawCenteredString(font, Component.literal("Warning!").withStyle(style -> style.withBold(true)), width / 2, top + 3, 0xFF5555);
        context.drawCenteredString(font, Component.translatable("warning.bounce_styles.missing.line1"), width / 2, top + 3 + (i++ * j), 0xFFFFFF);
        context.drawCenteredString(font, Component.translatable("warning.bounce_styles.missing.line2"), width / 2, top + 3 + (i++ * j), 0xFFFFFF);
        context.drawCenteredString(font, Component.translatable("warning.bounce_styles.missing.line3"), width / 2, top + 3 + (i++ * j), 0xFFFFFF);

        for(FormattedCharSequence text : font.split(Component.translatable("warning.bounce_styles.missing.line4", this.logFile.getName()), windowWidth - 10)) {
            i++;
            int textWidth = font.width(text);
            context.drawString(font, text, (width / 2) - (textWidth / 2), top + 3 + (i * j), 0xFFFFFF, false);
        }

        context.drawCenteredString(font, Component.literal("[ESC] Close this window").withStyle(style -> style.withColor(ChatFormatting.AQUA)), width / 2, top + windowHeight + 8, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(GuiGraphics context) {
        super.renderBackground(context);
        int right = left + windowWidth;
        int bottom = top + windowHeight;

        context.fillGradient(left, top, right, top + windowHeight, 0xcc00cccc, 0xcc175796);

        context.vLine(left, top - 1, bottom, 0xFF005454);
        context.vLine(left - 1, top - 2, bottom + 1, 0xFF00A8A8);
        context.vLine(left - 2, top - 3, bottom + 2, 0xFF005454);

        context.vLine(right, top - 1, bottom, 0xFF005454);
        context.vLine(right + 1, top - 2, bottom + 1, 0xFF00A8A8);
        context.vLine(right + 2, top - 3, bottom + 2, 0xFF005454);

        context.hLine(left - 2, right + 2, top - 3, 0xFF005454);
        context.hLine(left - 1, right + 1, top - 2, 0xFF00A8A8);
        context.hLine(left, right, top - 1, 0xFF005454);

        context.hLine(left, right, bottom, 0xFF005454);
        context.hLine(left - 1, right + 1, bottom + 1, 0xFF00A8A8);
        context.hLine(left - 2, right + 2, bottom + 2, 0xFF005454);
    }

    @Override
    public void onClose() {
        if(this.dontShowAgain.selected()) BounceStylesClient.addServerToIgnoreFile(this.hash);
        super.onClose();
    }
}
