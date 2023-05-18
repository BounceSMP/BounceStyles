package dev.bsmp.bouncestyles.networking.clientbound;

import dev.bsmp.bouncestyles.networking.StylePacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

public record SyncRegisteredStylesClientbound(Set<Identifier> identifiers) implements StylePacket.ClientboundStylePacket {
    public void encode(PacketByteBuf buf) {
        buf.writeInt(identifiers.size());
        for(Identifier id : identifiers) buf.writeIdentifier(id);
    }

    public static SyncRegisteredStylesClientbound decode(PacketByteBuf buf) {
        Set<Identifier> identifiers = new HashSet<>();
        int count = buf.readInt();
        for(int i = 0; i < count; i++) identifiers.add(buf.readIdentifier());
        return new SyncRegisteredStylesClientbound(identifiers);
    }
}
