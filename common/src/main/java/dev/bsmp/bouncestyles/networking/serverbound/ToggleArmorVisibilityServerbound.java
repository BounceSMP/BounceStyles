package dev.bsmp.bouncestyles.networking.serverbound;

import dev.bsmp.bouncestyles.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;

public record ToggleArmorVisibilityServerbound() implements StylePacket.ServerboundStylePacket {
    public void encode(FriendlyByteBuf buf) {}

    public static ToggleArmorVisibilityServerbound decode(FriendlyByteBuf buf) {
        return new ToggleArmorVisibilityServerbound();
    }
}
