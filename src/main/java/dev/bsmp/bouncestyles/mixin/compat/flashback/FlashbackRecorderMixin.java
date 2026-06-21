//? if fabric {
package dev.bsmp.bouncestyles.mixin.compat.flashback;
//~ avatar

import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.flashback.io.AsyncReplaySaver;
import com.moulberry.flashback.record.Recorder;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.fabric.compat.FlashbackCompat;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Recorder.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class FlashbackRecorderMixin {
    @Shadow @Final private AsyncReplaySaver asyncReplaySaver;

    @Inject(method = "writeSnapshot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isVehicle()Z"))
    private void checkAndWriteStyleData(boolean asActualSnapshot, CallbackInfo ci, @Local Entity entity, @Local(name = "gamePackets") List<Packet<? super ClientGamePacketListener>> gamePackets) {
        if (entity instanceof Avatar styleEntity) {
            if (!StyleData.hasStyleData(styleEntity)) return;

            var styleData = StyleData.getEntityData(styleEntity);
            asyncReplaySaver.submit(replayWriter -> {
                replayWriter.startAction(FlashbackCompat.StyleDataAction.INSTANCE);
                SyncStyleDataClientbound.STREAM_CODEC.encode(replayWriter.friendlyByteBuf(), new SyncStyleDataClientbound(entity.getId(), styleData));
                replayWriter.finishAction(FlashbackCompat.StyleDataAction.INSTANCE);
            });
        }
    }

}
//? }