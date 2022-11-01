package dev.bsmp.bouncestyles.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.bsmp.bouncestyles.client.BounceStylesClient;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    private void renderStyleItemType(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
        BounceStylesClient.drawStyleItemTypeOverlay(stack, x, y);
    }

}
