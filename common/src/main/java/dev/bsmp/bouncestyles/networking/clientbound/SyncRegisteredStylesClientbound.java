package dev.bsmp.bouncestyles.networking.clientbound;

import dev.bsmp.bouncestyles.networking.StylePacket;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SyncRegisteredStylesClientbound(Set<ResourceLocation> identifiers) implements StylePacket.ClientboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(identifiers.size());
        for(ResourceLocation id : identifiers) buf.writeResourceLocation(id);
    }

    public static SyncRegisteredStylesClientbound decode(FriendlyByteBuf buf) {
        Set<ResourceLocation> identifiers = new HashSet<>();
        int count = buf.readInt();
        for(int i = 0; i < count; i++) identifiers.add(buf.readResourceLocation());
        return new SyncRegisteredStylesClientbound(identifiers);
    }
}
