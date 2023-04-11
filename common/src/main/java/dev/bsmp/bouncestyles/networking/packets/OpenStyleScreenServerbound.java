package dev.bsmp.bouncestyles.networking.packets;

import net.minecraft.network.PacketByteBuf;

public record OpenStyleScreenServerbound() implements StylePacket.ServerboundStylePacket {
    public void encode(PacketByteBuf buf) {}

    public static OpenStyleScreenServerbound decode(PacketByteBuf buf) {
        return new OpenStyleScreenServerbound();
    }
}
