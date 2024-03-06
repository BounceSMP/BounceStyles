package dev.bsmp.bouncestyles.networking.clientbound;

import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;

public record SyncStyleDataClientbound(int entityId, StyleData styleData) implements StylePacket.ClientboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeNbt(StyleData.equippedToNBT(styleData));
    }

    public static SyncStyleDataClientbound decode(FriendlyByteBuf buf) {
        return new SyncStyleDataClientbound(
                buf.readInt(),
                StyleData.fromNBT(buf.readNbt())
        );
    }
}
