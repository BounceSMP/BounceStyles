package dev.bsmp.bouncestyles.core.networking.serverbound;

import dev.bsmp.bouncestyles.core.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;

public record OpenStyleScreenServerbound() implements StylePacket.ServerboundStylePacket {
    public void encode(FriendlyByteBuf buf) {}

    public static OpenStyleScreenServerbound decode(FriendlyByteBuf buf) {
        return new OpenStyleScreenServerbound();
    }
}
