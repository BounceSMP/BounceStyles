package dev.bsmp.bouncestyles.networking;

import dev.architectury.networking.NetworkChannel;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.networking.clientbound.SyncRegisteredStylesClientbound;
import dev.bsmp.bouncestyles.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.networking.clientbound.SyncStyleUnlocksClientbound;
import dev.bsmp.bouncestyles.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.networking.serverbound.OpenStyleScreenServerbound;
import dev.bsmp.bouncestyles.networking.serverbound.ToggleArmorVisibilityServerbound;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;

public class BounceStylesNetwork {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(new ResourceLocation(BounceStyles.modId, "network"));

    public static void initServerbound() {
        CHANNEL.register(EquipStyleServerbound.class, EquipStyleServerbound::encode, EquipStyleServerbound::decode, ServerPacketHandler::handleEquipStyle);
        CHANNEL.register(ToggleArmorVisibilityServerbound.class, ToggleArmorVisibilityServerbound::encode, ToggleArmorVisibilityServerbound::decode, ServerPacketHandler::handleArmorVisibility);
        CHANNEL.register(OpenStyleScreenServerbound.class, OpenStyleScreenServerbound::encode, OpenStyleScreenServerbound::decode, ServerPacketHandler::handleOpenStyleScreen);
    }

    public static void initClientbound() {
        CHANNEL.register(SyncStyleDataClientbound.class, SyncStyleDataClientbound::encode, SyncStyleDataClientbound::decode, (pkt, ctx) -> { ClientPacketHandler.handleSyncStyleData(pkt, ctx); });
        CHANNEL.register(SyncStyleUnlocksClientbound.class, SyncStyleUnlocksClientbound::encode, SyncStyleUnlocksClientbound::decode, (pkt, ctx) -> { ClientPacketHandler.handleSyncStyleUnlocks(pkt, ctx); });
        CHANNEL.register(SyncRegisteredStylesClientbound.class, SyncRegisteredStylesClientbound::encode, SyncRegisteredStylesClientbound::decode, (pkt, ctx) -> { ClientPacketHandler.handleSyncRegisteredStyles(pkt, ctx); });
    }

    public static void sendToTrackingPlayers(StylePacket.ClientboundStylePacket packet, Entity entity) {
        Set<ServerPlayerConnection> trackingPlayers = BounceStyles.getPlayersTracking(entity);
        for(ServerPlayerConnection tracker : trackingPlayers) {
            packet.sendToPlayer(tracker.getPlayer());
        }
    }
}
