package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.networking.SyncStyleUnlocksBi;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
    private static final Identifier TEX = new Identifier(BounceStyles.modId, "textures/icon/inv_btn.png");

    private InventoryScreenMixin(PlayerScreenHandler menu, PlayerInventory playerInventory, Text title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addWardrobeButton(CallbackInfo ci) {
        addDrawableChild(new TexturedButtonWidget(
                x + 78, y + 47, 13, 13,
                0, 0, 13, TEX, 13, 26,
                button -> SyncStyleUnlocksBi.sendToServer(),
                this::wardrobeTooltip,
                new LiteralText("Open Wardrobe")
        ));
    }

    private void wardrobeTooltip(ButtonWidget button, MatrixStack poseStack, int mouseX, int mouseY) {
        int textWidth = textRenderer.getWidth(button.getMessage());
        fill(poseStack, mouseX + 2, mouseY - 12, mouseX + textWidth + 7, mouseY + 2, 0xFF000000);

        drawVerticalLine(poseStack, mouseX + 1, mouseY - 13, mouseY + 3, 0xFF00A8A8);
        drawVerticalLine(poseStack, mouseX + textWidth + 7, mouseY - 13, mouseY + 2, 0xFF00A8A8);

        drawHorizontalLine(poseStack, mouseX + 1, mouseX + textWidth + 7, mouseY - 13, 0xFF00A8A8);
        drawHorizontalLine(poseStack, mouseX + 1, mouseX + textWidth + 7, mouseY + 2, 0xFF00A8A8);

        drawTextWithShadow(poseStack, textRenderer, button.getMessage(), mouseX + 5, mouseY - 9, 0xFFFFFF);
    }

}
