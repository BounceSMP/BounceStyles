package dev.bsmp.bouncestyles.core.networking.serverbound;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public record OpenStyleScreenServerbound() implements StylePacket.ServerboundStylePacket {
    public void encode(FriendlyByteBuf buf) {}

    public static OpenStyleScreenServerbound decode(FriendlyByteBuf buf) {
        return new OpenStyleScreenServerbound();
    }

    //? if neoforge {
    public static final Type<OpenStyleScreenServerbound> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(BounceStyles.resourceLocation("serverbound_open_wardrobe"));

    public static final net.minecraft.network.codec.StreamCodec<ByteBuf, OpenStyleScreenServerbound> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.unit(new OpenStyleScreenServerbound());

    @Override
    public Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
        return TYPE;
    }
    //?}
}
