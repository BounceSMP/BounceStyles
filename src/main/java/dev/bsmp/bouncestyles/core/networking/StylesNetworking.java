package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public class StylesNetworking {
    public static void initServerbound() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, EquipStyleServerbound.TYPE, EquipStyleServerbound.STREAM_CODEC, (packet, context) -> ServerPacketHandler.handleEquipStyle(packet, () -> context));
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, OpenStyleScreenServerbound.TYPE, OpenStyleScreenServerbound.STREAM_CODEC, (packet, context) -> ServerPacketHandler.handleOpenStyleScreen(packet, () -> context));
    }

    public static void initClientbound() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncStyleDataClientbound.TYPE, SyncStyleDataClientbound.STREAM_CODEC, (packet, context) -> ClientPacketHandler.handleSyncStyleData(packet, () -> context));
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, OpenWardrobeUIClientbound.TYPE, OpenWardrobeUIClientbound.STREAM_CODEC, (packet, context) -> ClientPacketHandler.handleOpenWardrobeUI(packet, () -> context));
        }
        else {
            NetworkManager.registerS2CPayloadType(SyncStyleDataClientbound.TYPE, SyncStyleDataClientbound.STREAM_CODEC);
            NetworkManager.registerS2CPayloadType(OpenWardrobeUIClientbound.TYPE, OpenWardrobeUIClientbound.STREAM_CODEC);
        }
    }

    public static void sendToTrackingPlayers(StylePacket.ClientboundStylePacket packet, Entity entity) {
        Set<ServerPlayerConnection> trackingPlayers = BounceStyles.getPlayersTracking(entity);
        for(ServerPlayerConnection tracker : trackingPlayers) {
            packet.sendToPlayer(tracker.getPlayer());
        }
    }
}