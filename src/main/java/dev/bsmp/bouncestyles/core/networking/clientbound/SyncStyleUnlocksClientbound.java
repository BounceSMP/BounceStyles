package dev.bsmp.bouncestyles.core.networking.clientbound;

import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;

public record SyncStyleUnlocksClientbound(StyleData styleData) implements StylePacket.ClientboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(StyleData.toNBT(styleData));
    }

    public static SyncStyleUnlocksClientbound decode(FriendlyByteBuf buf) {
        return new SyncStyleUnlocksClientbound(StyleData.fromNBT(buf.readNbt()));
    }
}
