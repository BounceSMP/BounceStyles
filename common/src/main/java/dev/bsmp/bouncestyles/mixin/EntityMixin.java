package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.BounceStyles;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "onStartedTrackingBy", at = @At("HEAD"))
    void syncStyle(ServerPlayerEntity player, CallbackInfo ci) {
        if(((Object) this) instanceof ServerPlayerEntity) {
            BounceStyles.startTrackingPlayer(player, (ServerPlayerEntity) ((Object) this));
        }
    }

}
