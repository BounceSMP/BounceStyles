package dev.bsmp.bouncestyles.networking.packets;

import net.minecraft.network.PacketByteBuf;

public record ToggleArmorVisibilityServerbound() implements StylePacket.ServerboundStylePacket {
    public void encode(PacketByteBuf buf) {}

    public static ToggleArmorVisibilityServerbound decode(PacketByteBuf buf) {
        return new ToggleArmorVisibilityServerbound();
    }
}
