package dev.bsmp.bouncestyles.mixin.client;

import dev.bsmp.bouncestyles.core.client.renderer.StyleEntityState;
import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.data.animation.AnimationHandler;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<E extends Avatar & ClientAvatarEntity> {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void bounceStyles$setupStyleStateData(E entity, AvatarRenderState state, float f, CallbackInfo ci) {
        if (entity instanceof Avatar avatar && state instanceof StyleEntityState styleState) {
            var styleData = StyleData.getEntityData(avatar);
            styleData.getHiddenParts().forEach(styleState::bounceStyles$addHiddenPart);

            var geoState = ((GeoRenderState) state);
            geoState.addGeckolibData(StyleLayerRenderer.TICKET_STYLE_DATA, styleData);
            geoState.addGeckolibData(AnimationHandler.TICKET_ON_GROUND, avatar.onGround());
            geoState.addGeckolibData(DataTickets.IS_MOVING, avatar.walkAnimation.speed() >= 0.015f);
            geoState.addGeckolibData(AnimationHandler.TICKET_SPRINTING, avatar.isSprinting());
        }
    }

}
