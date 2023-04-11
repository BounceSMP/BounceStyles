package dev.bsmp.bouncestyles.networking.packets;

import dev.bsmp.bouncestyles.data.StyleData;
import net.minecraft.network.PacketByteBuf;

public record SyncStyleUnlocksClientbound(StyleData styleData) implements StylePacket.ClientboundStylePacket {
    public void encode(PacketByteBuf buf) {
        buf.writeNbt(StyleData.toNBT(styleData));
    }

    public static SyncStyleUnlocksClientbound decode(PacketByteBuf buf) {
        return new SyncStyleUnlocksClientbound(StyleData.fromNBT(buf.readNbt()));
    }
}
