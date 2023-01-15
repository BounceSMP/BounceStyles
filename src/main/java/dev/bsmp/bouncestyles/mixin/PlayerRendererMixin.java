package dev.bsmp.bouncestyles.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.item.ItemStack;
import dev.bsmp.bouncestyles.item.StyleItem;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "setModelProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isCrouching()Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void checkStyleVisibility(AbstractClientPlayer player, CallbackInfo ci, PlayerModel<?> playerEntityModel) {
//        Optional<TrinketComponent> o = TrinketsApi.getTrinketComponent(player);
//        if(o.isPresent())
//            for(Tuple<SlotReference, ItemStack> pair : o.get().getEquipped(stack -> stack.getItem() instanceof StyleItem))
//                hideParts(playerEntityModel, (StyleItem) pair.getB().getItem());

        for(ItemStack itemStack : player.getArmorSlots())
            if(itemStack.getItem() instanceof StyleItem)
                hideParts(playerEntityModel, (StyleItem) itemStack.getItem());
    }

    private void hideParts(PlayerModel<?> playerEntityModel, StyleItem item) {
        if(item.hiddenParts != null && !item.hiddenParts.isEmpty()) {
            for(String s : item.hiddenParts) {
                switch (s) {
                    case "head" -> {
                        playerEntityModel.head.visible = false;
                        playerEntityModel.hat.visible = false;
                    }
                    case "body" -> {
                        playerEntityModel.body.visible = false;
                        playerEntityModel.jacket.visible = false;
                    }
                    case "left_arm" -> {
                        playerEntityModel.leftArm.visible = false;
                        playerEntityModel.leftSleeve.visible = false;
                    }
                    case "right_arm" -> {
                        playerEntityModel.rightArm.visible = false;
                        playerEntityModel.rightSleeve.visible = false;
                    }
                    case "left_leg" -> {
                        playerEntityModel.leftLeg.visible = false;
                        playerEntityModel.leftPants.visible = false;
                    }
                    case "right_leg" -> {
                        playerEntityModel.rightLeg.visible = false;
                        playerEntityModel.rightPants.visible = false;
                    }
                }
            }
        }
    }

}
