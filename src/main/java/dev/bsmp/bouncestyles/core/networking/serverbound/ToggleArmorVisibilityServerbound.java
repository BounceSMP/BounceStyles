package dev.bsmp.bouncestyles.core.networking.serverbound;

import dev.bsmp.bouncestyles.core.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;

public record ToggleArmorVisibilityServerbound(int index) implements StylePacket.ServerboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.index);
    }

    public static ToggleArmorVisibilityServerbound decode(FriendlyByteBuf buf) {
        return new ToggleArmorVisibilityServerbound(buf.readInt());
    }
}
