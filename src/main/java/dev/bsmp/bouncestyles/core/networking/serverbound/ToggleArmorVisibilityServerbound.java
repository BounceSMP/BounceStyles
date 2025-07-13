package dev.bsmp.bouncestyles.core.networking.serverbound;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public record ToggleArmorVisibilityServerbound(int index) implements StylePacket.ServerboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.index);
    }

    public static ToggleArmorVisibilityServerbound decode(FriendlyByteBuf buf) {
        return new ToggleArmorVisibilityServerbound(buf.readInt());
    }

    //? if neoforge {
    public static final Type<ToggleArmorVisibilityServerbound> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(BounceStyles.resourceLocation("serverbound_toggle_armour"));

    public static final net.minecraft.network.codec.StreamCodec<ByteBuf, ToggleArmorVisibilityServerbound> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.INT,
            ToggleArmorVisibilityServerbound::index,
            ToggleArmorVisibilityServerbound::new
    );

    @Override
    public Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
        return TYPE;
    }
    //?}
}
