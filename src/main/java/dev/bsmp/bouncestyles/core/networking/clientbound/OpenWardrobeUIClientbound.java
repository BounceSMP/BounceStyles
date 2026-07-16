package dev.bsmp.bouncestyles.core.networking.clientbound;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public record OpenWardrobeUIClientbound(Optional<List<Identifier>> unlocks) implements StylePacket.ClientboundStylePacket {

    //? if >= 1.21.1 {
    public static final Type<OpenWardrobeUIClientbound> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(BounceStyles.id("clientbound_open_wardrobe"));

    public static final net.minecraft.network.codec.StreamCodec<ByteBuf, OpenWardrobeUIClientbound> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.optional(net.minecraft.network.codec.ByteBufCodecs.fromCodec(Identifier.CODEC.listOf())),
            OpenWardrobeUIClientbound::unlocks,
            OpenWardrobeUIClientbound::new
    );

    @Override
    public Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
        return TYPE;
    }
    //? } else {
//    public void encode(FriendlyByteBuf buf) {
//        buf.writeJsonWithCodec(Identifier.CODEC.listOf(), unlocks);
//    }
//
//    public static OpenWardrobeUIClientbound decode(FriendlyByteBuf buf) {
//        //? if >= 1.21.11 {
//        return new OpenWardrobeUIClientbound(buf.readLenientJsonWithCodec(Identifier.CODEC.listOf()));
//        //? } else
//        //return new OpenWardrobeUIClientbound(buf.readJsonWithCodec(Identifier.CODEC.listOf()));
//    }
    //? }
}
