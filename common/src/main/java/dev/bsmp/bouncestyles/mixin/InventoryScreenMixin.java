package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.client.screen.widgets.WardrobeWidget;
import dev.bsmp.bouncestyles.networking.serverbound.OpenStyleScreenServerbound;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
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
                button -> new OpenStyleScreenServerbound().sendToServer(),
                Text.literal("Open Wardrobe")
        ));
    }
}
