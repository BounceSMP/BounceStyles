//? if fabric {
package dev.bsmp.bouncestyles.fabric.compat;

import com.moulberry.flashback.Flashback;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.mixin.compat.flashback.ReplayGamePacketHandlerAccessor;
import dev.bsmp.bouncestyles.mixin.compat.flashback.ReplayServerAccessor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class FlashbackCompat {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(SyncPacket.TYPE, SyncPacket.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(SyncPacket.TYPE, (payload, context) -> {
            if (Flashback.isInReplay())
                payload.handle();
        });
    }

    public record SyncPacket(int entityId, StyleData styleData) implements CustomPacketPayload {
        public static final Type<SyncPacket> TYPE = CustomPacketPayload.createType("bounce_styles_sync_style_data_flashback");
        public static final StreamCodec<FriendlyByteBuf, SyncPacket> STREAM_CODEC = new SyncCodec();

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public void handle() {
            var replayServer = Flashback.getReplayServer();
            if (replayServer != null) {
                BounceStyles.LOGGER.info("READING STYLEDATA - {}", entityId);
                var server = (ReplayServerAccessor) replayServer;
                var entity = ((ReplayGamePacketHandlerAccessor) server.getGamePacketHandler()).invokeGetEntityOrPending(entityId);

                if (entity instanceof Player player) {
                    BounceStyles.LOGGER.info("SETTING TO PLAYER - {}", player);
                    StyleData.setEntityData(player, styleData);
                }
            }
        }

        public static class SyncCodec implements StreamCodec<FriendlyByteBuf, SyncPacket> {
            @Override
            public SyncPacket decode(FriendlyByteBuf byteBuf) {
                return new SyncPacket(byteBuf.readInt(), StyleData.STREAM_CODEC.decode(byteBuf));
            }

            @Override
            public void encode(FriendlyByteBuf byteBuf, SyncPacket packet) {
                byteBuf.writeInt(packet.entityId());
                StyleData.STREAM_CODEC.encode(byteBuf, packet.styleData());
            }
        }
    }

}
//? }