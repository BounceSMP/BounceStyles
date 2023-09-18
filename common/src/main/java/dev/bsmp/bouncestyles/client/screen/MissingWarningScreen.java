package dev.bsmp.bouncestyles.client.screen;

import dev.bsmp.bouncestyles.client.BounceStylesClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;

public class MissingWarningScreen extends Screen {
    private final int windowWidth = 340;
    private final int windowHeight = 150;
    private int left, top;

    private CheckboxWidget dontShowAgain;
    private final File logFile;
    private final int hash;

    public MissingWarningScreen(File logFile, int hash) {
        super(Text.literal("Warning"));
        this.logFile = logFile;
        this.hash = hash;
    }

    @Override
    protected void init() {
        super.init();
        this.left = (width / 2) - (windowWidth / 2);
        this.top = (height / 2) - (windowHeight / 2);

        Text checkboxLabel = Text.translatable("gui.bounce_styles.missing.dont_show_again");
        dontShowAgain = addDrawableChild(new CheckboxWidget((width / 2) - 10 - (textRenderer.getWidth(checkboxLabel) / 2), top + windowHeight - 25, 20, 20, checkboxLabel, false));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        int i = 1;
        int j = 16;
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Warning!").styled(style -> style.withBold(true)), width / 2, top + 3, 0xFF5555);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("warning.bounce_styles.missing.line1"), width / 2, top + 3 + (i++ * j), 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("warning.bounce_styles.missing.line2"), width / 2, top + 3 + (i++ * j), 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("warning.bounce_styles.missing.line3"), width / 2, top + 3 + (i++ * j), 0xFFFFFF);

        for(OrderedText text : textRenderer.wrapLines(Text.translatable("warning.bounce_styles.missing.line4", this.logFile.getName()), windowWidth - 10)) {
            i++;
            int textWidth = textRenderer.getWidth(text);
            context.drawText(textRenderer, text, (width / 2) - (textWidth / 2), top + 3 + (i * j), 0xFFFFFF, false);
        }

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("[ESC] Close this window").styled(style -> style.withColor(Formatting.AQUA)), width / 2, top + windowHeight + 8, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
        int right = left + windowWidth;
        int bottom = top + windowHeight;

        context.fillGradient(left, top, right, top + windowHeight, 0xcc00cccc, 0xcc175796);

        context.drawVerticalLine(left, top - 1, bottom, 0xFF005454);
        context.drawVerticalLine(left - 1, top - 2, bottom + 1, 0xFF00A8A8);
        context.drawVerticalLine(left - 2, top - 3, bottom + 2, 0xFF005454);

        context.drawVerticalLine(right, top - 1, bottom, 0xFF005454);
        context.drawVerticalLine(right + 1, top - 2, bottom + 1, 0xFF00A8A8);
        context.drawVerticalLine(right + 2, top - 3, bottom + 2, 0xFF005454);

        context.drawHorizontalLine(left - 2, right + 2, top - 3, 0xFF005454);
        context.drawHorizontalLine(left - 1, right + 1, top - 2, 0xFF00A8A8);
        context.drawHorizontalLine(left, right, top - 1, 0xFF005454);

        context.drawHorizontalLine(left, right, bottom, 0xFF005454);
        context.drawHorizontalLine(left - 1, right + 1, bottom + 1, 0xFF00A8A8);
        context.drawHorizontalLine(left - 2, right + 2, bottom + 2, 0xFF005454);
    }

    @Override
    public void close() {
        if(this.dontShowAgain.isChecked()) BounceStylesClient.addServerToIgnoreFile(this.hash);
        super.close();
    }
}
