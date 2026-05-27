package dev.bsmp.bouncestyles.mixin.client;

import dev.bsmp.bouncestyles.core.client.renderer.StyleEntityState;
import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
import dev.bsmp.bouncestyles.core.data.StyleData;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin<E extends Avatar & ClientAvatarEntity> {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void bounceStyles$setupStyleStateData(E entity, AvatarRenderState state, float f, CallbackInfo ci) {
        if (entity instanceof Player player && state instanceof StyleEntityState styleState) {
            var styleData = StyleData.getEntityData(player);
            styleData.getHiddenParts().forEach(styleState::bounceStyles$addHiddenPart);
            state.addGeckolibData(StyleLayerRenderer.TICKET_STYLE_DATA, styleData);
        }
    }

}
