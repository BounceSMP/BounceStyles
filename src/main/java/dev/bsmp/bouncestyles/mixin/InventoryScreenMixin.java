package dev.bsmp.bouncestyles.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.networking.SyncStyleUnlocksBi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {
    private static final ResourceLocation TEX = new ResourceLocation(BounceStyles.modId, "textures/icon/inv_btn.png");

    private InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addWardrobeButton(CallbackInfo ci) {
        addRenderableWidget(new ImageButton(
                leftPos + 78, topPos + 47, 13, 13,
                0, 0, 13, TEX, 13, 26,
                button -> SyncStyleUnlocksBi.sendToServer(),
                this::wardrobeTooltip,
                new TextComponent("Open Wardrobe")
        ));
    }

    private void wardrobeTooltip(Button button, PoseStack poseStack, int mouseX, int mouseY) {
        int textWidth = font.width(button.getMessage());
        fill(poseStack, mouseX + 2, mouseY - 12, mouseX + textWidth + 7, mouseY + 2, 0xFF000000);

        vLine(poseStack, mouseX + 1, mouseY - 13, mouseY + 3, 0xFF00A8A8);
        vLine(poseStack, mouseX + textWidth + 7, mouseY - 13, mouseY + 2, 0xFF00A8A8);

        hLine(poseStack, mouseX + 1, mouseX + textWidth + 7, mouseY - 13, 0xFF00A8A8);
        hLine(poseStack, mouseX + 1, mouseX + textWidth + 7, mouseY + 2, 0xFF00A8A8);

        drawString(poseStack, font, button.getMessage(), mouseX + 5, mouseY - 9, 0xFFFFFF);
    }

}
