package dev.bsmp.bouncestyles.core.networking.serverbound;

import dev.bsmp.bouncestyles.api.style.StylePreset;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Optional;

public record UpdatePresetServerbound(String presetName, Optional<StylePreset> preset) implements StylePacket.ServerboundStylePacket {
    public UpdatePresetServerbound(String presetName, StylePreset preset) {
        this(presetName, Optional.of(preset));
    }
    public UpdatePresetServerbound(String presetName) {
        this(presetName, Optional.empty());
    }

    public static final CustomPacketPayload.Type<UpdatePresetServerbound> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(BounceStyles.id("serverbound_update_preset"));

    public static final net.minecraft.network.codec.StreamCodec<ByteBuf, UpdatePresetServerbound> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UpdatePresetServerbound::presetName,
            ByteBufCodecs.optional(ByteBufCodecs.fromCodec(StylePreset.CODEC)), UpdatePresetServerbound::preset,
            UpdatePresetServerbound::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
