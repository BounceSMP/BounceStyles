package dev.bsmp.bouncestyles.mixin.compat.flashback;

import com.moulberry.flashback.record.IgnoredPacketSet;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IgnoredPacketSet.class)
public abstract class IgnoredPacketSetMixin {

    @Inject(method = "isIgnored", at = @At("HEAD"), cancellable = true)
    private static void bounceStyles$packetCheck(Packet<?> packet, CallbackInfoReturnable<Boolean> cir) {
        if (packet instanceof ClientboundCustomPayloadPacket payloadPacket) {
            if (payloadPacket.payload().type().id() == OpenWardrobeUIClientbound.TYPE.id()) {
                cir.setReturnValue(true);
            }
        }
    }

}
