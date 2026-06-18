package dev.bsmp.bouncestyles.mixin.compat.flashback;
//~ avatar

import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.flashback.record.Recorder;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if fabric {
//? }

import java.util.List;

@Mixin(Recorder.class)
@MixinEnvironment()
public abstract class FlashbackRecorderMixin {

    @Inject(method = "writeSnapshot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isVehicle()Z"))
    private void checkAndWriteStyleData(boolean asActualSnapshot, CallbackInfo ci, @Local Entity entity, @Local(name = "gamePackets") List<Packet<? super ClientGamePacketListener>> gamePackets) {
        if (asActualSnapshot && entity instanceof Avatar styleEntity) {
            if (!StyleData.hasStyleData(styleEntity)) return;

            var styleData = StyleData.getEntityData(styleEntity);
            gamePackets.add(
                    new ClientboundCustomPayloadPacket(new SyncStyleDataClientbound(styleEntity.getId(), styleData))
            );
        }
    }

}
