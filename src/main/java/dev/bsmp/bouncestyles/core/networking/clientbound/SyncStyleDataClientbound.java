package dev.bsmp.bouncestyles.core.networking.clientbound;

import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
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
