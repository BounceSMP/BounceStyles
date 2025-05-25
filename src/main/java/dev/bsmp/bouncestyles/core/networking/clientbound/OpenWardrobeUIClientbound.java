package dev.bsmp.bouncestyles.core.networking.clientbound;

import dev.bsmp.bouncestyles.core.networking.StylePacket;
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
}
