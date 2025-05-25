package dev.bsmp.bouncestyles.core.networking.clientbound;

import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
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
}
