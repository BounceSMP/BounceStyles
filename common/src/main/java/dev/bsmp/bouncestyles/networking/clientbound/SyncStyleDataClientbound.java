package dev.bsmp.bouncestyles.networking.clientbound;

import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.networking.StylePacket;
import net.minecraft.network.PacketByteBuf;

public record SyncStyleDataClientbound(int entityId, StyleData styleData) implements StylePacket.ClientboundStylePacket {
    public void encode(PacketByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeNbt(StyleData.equippedToNBT(styleData));
    }

    public static SyncStyleDataClientbound decode(PacketByteBuf buf) {
        return new SyncStyleDataClientbound(
                buf.readInt(),
                StyleData.fromNBT(buf.readNbt())
        );
    }
}
