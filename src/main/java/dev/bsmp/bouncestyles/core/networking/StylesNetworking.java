package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncPresetsClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.UpdatePresetServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.RequestPresetsServerbound;
import dev.bsmp.bouncestyles.mixin.common.ChunkStorageAccessor;
import dev.bsmp.bouncestyles.mixin.common.EntityTrackerAccessor;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkSource;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;

public class StylesNetworking {
    public static void initServerbound() {
        registerServerbound(EquipStyleServerbound.TYPE, EquipStyleServerbound.STREAM_CODEC, ServerPacketHandler::handleEquipStyle);
        registerServerbound(OpenStyleScreenServerbound.TYPE, OpenStyleScreenServerbound.STREAM_CODEC, ServerPacketHandler::handleOpenStyleScreen);
        registerServerbound(RequestPresetsServerbound.TYPE, RequestPresetsServerbound.STREAM_CODEC, ServerPacketHandler::handleRequestPresets);
        registerServerbound(UpdatePresetServerbound.TYPE, UpdatePresetServerbound.STREAM_CODEC, ServerPacketHandler::handleCreatePreset);
    }

    public static void initClientbound() {
        registerClientbound(SyncStyleDataClientbound.TYPE, SyncStyleDataClientbound.STREAM_CODEC, (context, packet) ->  ClientPacketHandler.handleSyncStyleData(context, packet));
        registerClientbound(OpenWardrobeUIClientbound.TYPE, OpenWardrobeUIClientbound.STREAM_CODEC, (context, packet) ->  ClientPacketHandler.handleOpenWardrobeUI(context, packet));
        registerClientbound(SyncPresetsClientbound.TYPE, SyncPresetsClientbound.STREAM_CODEC, (context, packet) ->  ClientPacketHandler.handleSyncPresets(context, packet));
    }

    private static <T extends StylePacket.ServerboundStylePacket> void registerServerbound(CustomPacketPayload.Type<T> type, StreamCodec<ByteBuf, T> streamCodec, BiConsumer<NetworkManager.PacketContext, T> handler) {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, type, streamCodec, (packet, context) -> context.queue(() -> handler.accept(context, packet)));
    }

    private static <T extends StylePacket.ClientboundStylePacket> void registerClientbound(CustomPacketPayload.Type<T> type, StreamCodec<ByteBuf, T> streamCodec, BiConsumer<NetworkManager.PacketContext, T> handler) {
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, type, streamCodec, (packet, context) -> context.queue(() -> handler.accept(context, packet)));
        }
        else {
            NetworkManager.registerS2CPayloadType(type, streamCodec);
        }
    }

    public static void sendToTrackingPlayers(StylePacket.ClientboundStylePacket packet, Entity entity) {
        Set<ServerPlayerConnection> trackingPlayers = getPlayersTracking(entity);
        for(ServerPlayerConnection tracker : trackingPlayers) {
            packet.sendToPlayer(tracker.getPlayer());
        }
    }

    public static Set<ServerPlayerConnection> getPlayersTracking(Entity entity) {
        ChunkSource manager = entity.level().getChunkSource();
        if (manager instanceof ServerChunkCache) {
            ChunkMap storage = ((ServerChunkCache) manager).chunkMap;
            EntityTrackerAccessor tracker = ((ChunkStorageAccessor) storage).getEntityTrackers().get(entity.getId());

            if(tracker != null) {
                return tracker.getPlayersTracking();
            }
        }
        return Collections.emptySet();
    }
}