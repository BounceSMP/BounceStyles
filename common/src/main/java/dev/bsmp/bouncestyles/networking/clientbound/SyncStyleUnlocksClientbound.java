package dev.bsmp.bouncestyles.networking.clientbound;

import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;

public record SyncStyleUnlocksClientbound(StyleData styleData) implements StylePacket.ClientboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(StyleData.toNBT(styleData));
    }

    public static SyncStyleUnlocksClientbound decode(FriendlyByteBuf buf) {
        return new SyncStyleUnlocksClientbound(StyleData.fromNBT(buf.readNbt()));
    }
}
