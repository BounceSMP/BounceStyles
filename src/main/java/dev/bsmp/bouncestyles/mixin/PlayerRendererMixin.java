package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.data.StyleData;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "setModelPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isInSneakingPose()Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void checkStyleVisibility(AbstractClientPlayerEntity player, CallbackInfo ci, PlayerEntityModel<?> model) {
        StyleData styleData = StyleData.getPlayerData(player);
        for(String s : styleData.getHiddenParts()) {
            switch (s) {
                case "head" -> {
                    model.head.visible = false;
                    model.hat.visible = false;
                }
                case "body" -> {
                    model.body.visible = false;
                    model.jacket.visible = false;
                    ((PlayerModelAccessor)model).getCloak().visible = false;
                }
                case "left_arm" -> {
                    model.leftArm.visible = false;
                    model.leftSleeve.visible = false;
                }
                case "right_arm" -> {
                    model.rightArm.visible = false;
                    model.rightSleeve.visible = false;
                }
                case "left_leg" -> {
                    model.leftLeg.visible = false;
                    model.leftPants.visible = false;
                }
                case "right_leg" -> {
                    model.rightLeg.visible = false;
                    model.rightPants.visible = false;
                }
            }
        }
    }

}
