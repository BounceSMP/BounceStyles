package dev.bsmp.bouncestyles.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

import dev.bsmp.bouncestyles.item.StyleItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "setModelPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isInSneakingPose()Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void checkStyleVisibility(AbstractClientPlayerEntity player, CallbackInfo ci, PlayerEntityModel<?> playerEntityModel) {
        Optional<TrinketComponent> o = TrinketsApi.getTrinketComponent(player);
        if(o.isPresent())
            for(Pair<SlotReference, ItemStack> pair : o.get().getEquipped(stack -> stack.getItem() instanceof StyleItem))
                hideParts(playerEntityModel, (StyleItem) pair.getRight().getItem());

        for(ItemStack itemStack : player.getArmorItems())
            if(itemStack.getItem() instanceof StyleItem)
                hideParts(playerEntityModel, (StyleItem) itemStack.getItem());
    }

    private void hideParts(PlayerEntityModel<?> playerEntityModel, StyleItem item) {
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
