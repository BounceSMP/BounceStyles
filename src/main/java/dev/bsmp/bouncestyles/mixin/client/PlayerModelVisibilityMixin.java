package dev.bsmp.bouncestyles.mixin.client;

//? if >= 1.21.5 {
import dev.bsmp.bouncestyles.core.client.renderer.StyleEntityState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
//? } else {
/*import dev.bsmp.bouncestyles.api.data.StyleData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
*///? }

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PlayerModel.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
//? if >= 1.21.5 {
public abstract class PlayerModelVisibilityMixin extends HumanoidModel<AvatarRenderState> {
//? } else
//public abstract class PlayerModelVisibilityMixin extends HumanoidModel<Player> {
    @Shadow @Final public ModelPart jacket;
    @Shadow @Final public ModelPart leftSleeve;
    @Shadow @Final public ModelPart rightSleeve;
    @Shadow @Final public ModelPart leftPants;
    @Shadow @Final public ModelPart rightPants;

    //? if >= 1.21.5 {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
    private void bounceStyles$checkStyleVisibility(AvatarRenderState state, CallbackInfo ci) {
        this.head.visible = true; //Heads don't automatically get set back to visible I guess?
        if (state instanceof StyleEntityState styleState) {
            bounceStyles$applyVisibility(styleState.bounceStyles$getHiddenParts());
        }
    }
    //? } else {
    /*@Shadow @Final private ModelPart cloak;

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void bounceStyles$checkStyleVisibility(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        StyleData styleData = StyleData.getEntityData((Player) entity);
        bounceStyles$applyVisibility(styleData.getHiddenParts());
    }
    *///? }

    @Unique
    private void bounceStyles$applyVisibility(Set<String> hiddenParts) {
        this.head.visible = true; //Head sometimes doesn't get reset, related to spectator mode.

        for (String s : hiddenParts) {
            switch (s) {
                case "head" -> {
                    this.head.visible = false;
                    this.hat.visible = false;
                }
                case "body" -> {
                    this.body.visible = false;
                    this.jacket.visible = false;
                    //? if < 1.21.5
                    //this.cloak.visible = false; //ToDo Later Versions move capes to their own layer
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

    private PlayerModelVisibilityMixin(ModelPart root) {
        super(root);
    }
}
