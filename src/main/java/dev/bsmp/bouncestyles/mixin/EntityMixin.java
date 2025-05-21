package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.core.BounceStyles;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "startSeenByPlayer", at = @At("HEAD"))
    void syncStyle(ServerPlayer player, CallbackInfo ci) {
        if(((Object) this) instanceof ServerPlayer) {
            BounceStyles.startTrackingPlayer(player, (ServerPlayer) ((Object) this));
        }
    }

}
