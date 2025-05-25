package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkChannel;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.ToggleArmorVisibilityServerbound;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public class BounceStylesNetwork {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(BounceStyles.resourceLocation("network"));

    public static void initServerbound() {
        CHANNEL.register(EquipStyleServerbound.class, EquipStyleServerbound::encode, EquipStyleServerbound::decode, ServerPacketHandler::handleEquipStyle);
        CHANNEL.register(ToggleArmorVisibilityServerbound.class, ToggleArmorVisibilityServerbound::encode, ToggleArmorVisibilityServerbound::decode, ServerPacketHandler::handleArmorVisibility);
        CHANNEL.register(OpenStyleScreenServerbound.class, OpenStyleScreenServerbound::encode, OpenStyleScreenServerbound::decode, ServerPacketHandler::handleOpenStyleScreen);
    }

    public static void initClientbound() {
        CHANNEL.register(SyncStyleDataClientbound.class, SyncStyleDataClientbound::encode, SyncStyleDataClientbound::decode, (pkt, ctx) -> { ClientPacketHandler.handleSyncStyleData(pkt, ctx); });
        CHANNEL.register(OpenWardrobeUIClientbound.class, OpenWardrobeUIClientbound::encode, OpenWardrobeUIClientbound::decode, (pkt, ctx) -> { ClientPacketHandler.handleOpenWardrobeUI(pkt, ctx); });
    }

    public static void sendToTrackingPlayers(StylePacket.ClientboundStylePacket packet, Entity entity) {
        Set<ServerPlayerConnection> trackingPlayers = BounceStyles.getPlayersTracking(entity);
        for(ServerPlayerConnection tracker : trackingPlayers) {
            packet.sendToPlayer(tracker.getPlayer());
        }
    }
}
