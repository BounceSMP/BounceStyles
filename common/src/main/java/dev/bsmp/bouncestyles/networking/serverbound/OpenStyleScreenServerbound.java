package dev.bsmp.bouncestyles.networking.serverbound;

import dev.bsmp.bouncestyles.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;

public record OpenStyleScreenServerbound() implements StylePacket.ServerboundStylePacket {
    public void encode(FriendlyByteBuf buf) {}

    public static OpenStyleScreenServerbound decode(FriendlyByteBuf buf) {
        return new OpenStyleScreenServerbound();
    }
}
