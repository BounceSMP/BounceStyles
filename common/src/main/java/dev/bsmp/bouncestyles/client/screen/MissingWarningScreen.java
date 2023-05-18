package dev.bsmp.bouncestyles.client.screen;

import dev.bsmp.bouncestyles.client.BounceStylesClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
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
        super(new LiteralText("Warning"));
        this.logFile = logFile;
        this.hash = hash;
    }

    @Override
    protected void init() {
        super.init();
        this.left = (width / 2) - (windowWidth / 2);
        this.top = (height / 2) - (windowHeight / 2);

        TranslatableText checkboxLabel = new TranslatableText("gui.bounce_styles.missing.dont_show_again");
        dontShowAgain = addDrawableChild(new CheckboxWidget((width / 2) - 10 - (textRenderer.getWidth(checkboxLabel) / 2), top + windowHeight - 25, 20, 20, checkboxLabel, false));
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float delta) {
        renderBackground(poseStack);
        int i = 1;
        int j = 16;
        drawCenteredText(poseStack, textRenderer, new LiteralText("Warning!").styled(style -> style.withBold(true)), width / 2, top + 3, 0xFF5555);
        drawCenteredText(poseStack, textRenderer, new TranslatableText("warning.bounce_styles.missing.line1"), width / 2, top + 3 + (i++ * j), 0xFFFFFF);
        drawCenteredText(poseStack, textRenderer, new TranslatableText("warning.bounce_styles.missing.line2"), width / 2, top + 3 + (i++ * j), 0xFFFFFF);
        drawCenteredText(poseStack, textRenderer, new TranslatableText("warning.bounce_styles.missing.line3"), width / 2, top + 3 + (i++ * j), 0xFFFFFF);

        for(OrderedText text : textRenderer.wrapLines(new TranslatableText("warning.bounce_styles.missing.line4", this.logFile.getName()), windowWidth - 10)) {
            i++;
            int textWidth = textRenderer.getWidth(text);
            textRenderer.draw(poseStack, text, (width / 2) - (textWidth / 2), top + 3 + (i * j), 0xFFFFFF);
        }

        drawCenteredText(poseStack, textRenderer, new LiteralText("[ESC] Close this window").styled(style -> style.withColor(Formatting.AQUA)), width / 2, top + windowHeight + 8, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(MatrixStack poseStack) {
        super.renderBackground(poseStack);
        int right = left + windowWidth;
        int bottom = top + windowHeight;

        fillGradient(poseStack, left, top, right, top + windowHeight, 0xcc00cccc, 0xcc175796);

        drawVerticalLine(poseStack, left, top - 1, bottom, 0xFF005454);
        drawVerticalLine(poseStack, left - 1, top - 2, bottom + 1, 0xFF00A8A8);
        drawVerticalLine(poseStack, left - 2, top - 3, bottom + 2, 0xFF005454);

        drawVerticalLine(poseStack, right, top - 1, bottom, 0xFF005454);
        drawVerticalLine(poseStack, right + 1, top - 2, bottom + 1, 0xFF00A8A8);
        drawVerticalLine(poseStack, right + 2, top - 3, bottom + 2, 0xFF005454);

        drawHorizontalLine(poseStack, left - 2, right + 2, top - 3, 0xFF005454);
        drawHorizontalLine(poseStack, left - 1, right + 1, top - 2, 0xFF00A8A8);
        drawHorizontalLine(poseStack, left, right, top - 1, 0xFF005454);

        drawHorizontalLine(poseStack, left, right, bottom, 0xFF005454);
        drawHorizontalLine(poseStack, left - 1, right + 1, bottom + 1, 0xFF00A8A8);
        drawHorizontalLine(poseStack, left - 2, right + 2, bottom + 2, 0xFF005454);
    }

    @Override
    public void close() {
        if(this.dontShowAgain.isChecked()) BounceStylesClient.addServerToIgnoreFile(this.hash);
        super.close();
    }
}
