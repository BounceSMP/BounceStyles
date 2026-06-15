package dev.bsmp.bouncestyles.mixin.client;

import dev.bsmp.bouncestyles.core.client.renderer.StyleEntityState;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >= 1.21.11 {
import net.minecraft.client.model.player.PlayerModel;
//? } else {
//import net.minecraft.client.model.PlayerModel;
//import net.minecraft.client.renderer.entity.player.PlayerRenderer;
//import dev.bsmp.bouncestyles.core.data.StyleData;
//import net.minecraft.world.entity.player.Player;
//? }

//? if >= 1.21.11 {
@Mixin(PlayerModel.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class PlayerModelVisibilityMixin extends HumanoidModel<AvatarRenderState> {
    @Shadow @Final public ModelPart jacket;
    @Shadow @Final public ModelPart leftSleeve;
    @Shadow @Final public ModelPart rightSleeve;
    @Shadow @Final public ModelPart leftPants;
    @Shadow @Final public ModelPart rightPants;

    private PlayerModelVisibilityMixin(ModelPart root) {
        super(root);
    }
//? } else {
//@Mixin(PlayerRenderer.class)
//public abstract class PlayerModelVisibilityMixin {
//? }

    //? if >= 1.21.11 {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
    private void bounceStyles$checkStyleVisibility(AvatarRenderState state, CallbackInfo ci) {
        this.head.visible = true; //Heads don't automatically get set back to visible I guess?
        if (state instanceof StyleEntityState styleState) {
            for (String s : styleState.bounceStyles$getHiddenParts()) {
                switch (s) {
                    case "head" -> {
                        this.head.visible = false;
                        this.hat.visible = false;
                    }
                    case "body" -> {
                        this.body.visible = false;
                        this.jacket.visible = false;
                    }
                    case "left_arm" -> {
                        this.leftArm.visible = false;
                        this.leftSleeve.visible = false;
                    }
                    case "right_arm" -> {
                        this.rightArm.visible = false;
                        this.rightSleeve.visible = false;
                    }
                    case "left_leg" -> {
                        this.leftLeg.visible = false;
                        this.leftPants.visible = false;
                    }
                    case "right_leg" -> {
                        this.rightLeg.visible = false;
                        this.rightPants.visible = false;
                    }
                }
            }
        }
    }
    //? } else {
//    @Inject(method = "setModelProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isCrouching()Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
//    private void bounceStyles$checkStyleVisibility(AbstractClientPlayer player, CallbackInfo ci, PlayerModel<?> model) {
//        StyleData styleData = StyleData.getOrCreateStyleData(player);
//        for(String s : styleData.getHiddenParts()) {
//        switch (s) {
//            case "head" -> {
//                model.head.visible = false;
//                model.hat.visible = false;
//            }
//            case "body" -> {
//                model.body.visible = false;
//                model.jacket.visible = false;
//                ((PlayerModelAccessor)model).getCloak().visible = false;
//            }
//            case "left_arm" -> {
//                model.leftArm.visible = false;
//                model.leftSleeve.visible = false;
//            }
//            case "right_arm" -> {
//                model.rightArm.visible = false;
//                model.rightSleeve.visible = false;
//            }
//            case "left_leg" -> {
//                model.leftLeg.visible = false;
//                model.leftPants.visible = false;
//            }
//            case "right_leg" -> {
//                model.rightLeg.visible = false;
//                model.rightPants.visible = false;
//            }
//        }
//    }
//    }
    //? }



}
