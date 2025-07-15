package dev.bsmp.bouncestyles.core.networking.clientbound;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record OpenWardrobeUIClientbound(List<ResourceLocation> unlocks) implements StylePacket.ClientboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(ResourceLocation.CODEC.listOf(), unlocks);
    }

    public static OpenWardrobeUIClientbound decode(FriendlyByteBuf buf) {
        return new OpenWardrobeUIClientbound(buf.readJsonWithCodec(ResourceLocation.CODEC.listOf()));
    }

    //? if >= 1.21.1 {
    public static final Type<OpenWardrobeUIClientbound> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(BounceStyles.resourceLocation("clientbound_open_wardrobe"));

    public static final net.minecraft.network.codec.StreamCodec<ByteBuf, OpenWardrobeUIClientbound> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.fromCodec(ResourceLocation.CODEC.listOf()),
            OpenWardrobeUIClientbound::unlocks,
            OpenWardrobeUIClientbound::new
    );

    @Override
    public Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
        return TYPE;
    }
    //?}
}
