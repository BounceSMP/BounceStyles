package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.core.client.screen.widgets.button.WardrobeIconButton;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >= 1.21.11 {
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
//? } else
//import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;

@Mixin(InventoryScreen.class)
//? if >= 1.21.11 {
public abstract class InventoryScreenMixin  extends AbstractRecipeBookScreen<InventoryMenu> {
//? } else
//public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {

    //? if >= 1.21.11 {
    private InventoryScreenMixin(InventoryMenu menu, RecipeBookComponent<?> recipeBookComponent, Inventory playerInventory, Component title) {
        super(menu, recipeBookComponent, playerInventory, title);
    }
    //? } else {
//    private InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
//        super(menu, playerInventory, title);
//    }
    //? }

    @Inject(method = "init", at = @At("TAIL"))
    private void addWardrobeButton(CallbackInfo ci) {
        addRenderableWidget(new WardrobeIconButton(
                leftPos + 78, topPos + 47,
                "btn_inventory",
                Component.literal("Open Wardrobe"),
                button -> new OpenStyleScreenServerbound().sendToServer()
        ));
    }
}
