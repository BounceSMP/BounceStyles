package dev.bsmp.bouncestyles.core.networking.clientbound;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncStyleDataClientbound(int entityId, StyleData styleData) implements StylePacket.ClientboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeJsonWithCodec(StyleData.CODEC, styleData);
    }

    public static SyncStyleDataClientbound decode(FriendlyByteBuf buf) {
        //? if >= 1.21.11 {
        return new SyncStyleDataClientbound(buf.readInt(), buf.readLenientJsonWithCodec(StyleData.CODEC));
        //? } else
        //return new SyncStyleDataClientbound(buf.readInt(), buf.readJsonWithCodec(StyleData.CODEC));
    }

    //? if >= 1.21.1 {
    public static final Type<SyncStyleDataClientbound> TYPE = new CustomPacketPayload.Type<>(BounceStyles.id("clientbound_sync_style_data"));

    public static final StreamCodec<ByteBuf, SyncStyleDataClientbound> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.composite(
            ByteBufCodecs.INT,
            SyncStyleDataClientbound::entityId,
            ByteBufCodecs.fromCodec(StyleData.CODEC),
            SyncStyleDataClientbound::styleData,
            SyncStyleDataClientbound::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    //?}
}
