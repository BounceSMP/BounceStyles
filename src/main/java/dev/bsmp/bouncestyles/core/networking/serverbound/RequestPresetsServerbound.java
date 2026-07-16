package dev.bsmp.bouncestyles.core.networking.serverbound;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RequestPresetsServerbound() implements StylePacket.ServerboundStylePacket {
    public static final CustomPacketPayload.Type<RequestPresetsServerbound> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(BounceStyles.id("serverbound_request_presets"));

    public static final net.minecraft.network.codec.StreamCodec<ByteBuf, RequestPresetsServerbound> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.unit(new RequestPresetsServerbound());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
