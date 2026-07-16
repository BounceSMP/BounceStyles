package dev.bsmp.bouncestyles.core.networking.clientbound;

import dev.bsmp.bouncestyles.api.style.StylePreset;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.data.preset.ServerPresets;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Map;

public record SyncPresetsClientbound(Map<String, StylePreset> globalPresets, Map<String, StylePreset> playerPresets) implements StylePacket.ClientboundStylePacket {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final Type<SyncPresetsClientbound> TYPE = new Type<>(BounceStyles.id("clientbound_sync_presets"));
    public static final StreamCodec<ByteBuf, SyncPresetsClientbound> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(ServerPresets.MAP_CODEC), SyncPresetsClientbound::globalPresets,
            ByteBufCodecs.fromCodec(ServerPresets.MAP_CODEC), SyncPresetsClientbound::playerPresets,
            SyncPresetsClientbound::new
    );
}
