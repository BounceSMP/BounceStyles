package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface StylePacket /*? if >= 1.21.1 {*/ extends net.minecraft.network.protocol.common.custom.CustomPacketPayload /*?}*/ {

    interface ServerboundStylePacket extends StylePacket {
        default void sendToServer() {
            //? if <= 1.20.1 {
            /*StylesLegacyNetworking.CHANNEL.sendToServer(this);
            *///?} else {
            NetworkManager.sendToServer(this);
            //?}
            //net.neoforged.neoforge.network.PacketDistributor.sendToServer(this);
        }
    }

    interface ClientboundStylePacket extends StylePacket {
        default void sendToPlayer(ServerPlayer player) {
            //? if <= 1.20.1 {
            /*StylesLegacyNetworking.CHANNEL.sendToPlayer(player, this);
            *///?} else {
            NetworkManager.sendToPlayer(player, this);
            //?}
            //net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, this);
        }

        default void sendToPlayers(Iterable<ServerPlayer> players) {
            players.forEach(this::sendToPlayer);
        }

        default void sendToTrackingPlayers(Entity entity) {
            //? if <= 1.20.1 {
            /*StylesLegacyNetworking.sendToTrackingPlayers(this, entity);
            *///?} elif >= 1.21.1 {
            StylesNetworking.sendToTrackingPlayers(this, entity);
            //?}
        }
    }
}
