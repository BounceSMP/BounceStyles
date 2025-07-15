package dev.bsmp.bouncestyles.core.networking.clientbound;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public record SyncStyleDataClientbound(int entityId, StyleData styleData) implements StylePacket.ClientboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeJsonWithCodec(StyleData.CODEC_EQUIPPED, styleData);
    }

    public static SyncStyleDataClientbound decode(FriendlyByteBuf buf) {
        return new SyncStyleDataClientbound(
                buf.readInt(),
                buf.readJsonWithCodec(StyleData.CODEC_EQUIPPED)
        );
    }

    //? if >= 1.21.1 {
    public static final Type<SyncStyleDataClientbound> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(BounceStyles.resourceLocation("clientbound_sync_style_data"));

    public static final net.minecraft.network.codec.StreamCodec<ByteBuf, SyncStyleDataClientbound> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.INT,
            SyncStyleDataClientbound::entityId,
            net.minecraft.network.codec.ByteBufCodecs.fromCodec(StyleData.CODEC_EQUIPPED),
            SyncStyleDataClientbound::styleData,
            SyncStyleDataClientbound::new
    );

    @Override
    public Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
        return TYPE;
    }
    //?}
}
