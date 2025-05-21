package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {
    private static final ResourceLocation TEX = BounceStyles.resourceLocation("textures/icon/inv_btn.png");

    private InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addWardrobeButton(CallbackInfo ci) {
        addRenderableWidget(new ImageButton(
                leftPos + 78, topPos + 47, 13, 13,
                0, 0, 13, TEX, 13, 26,
                button -> new OpenStyleScreenServerbound().sendToServer(),
                Component.literal("Open Wardrobe")
        ));
    }
}
