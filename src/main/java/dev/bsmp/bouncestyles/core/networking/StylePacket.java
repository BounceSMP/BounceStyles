package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface StylePacket extends net.minecraft.network.protocol.common.custom.CustomPacketPayload {
    interface ServerboundStylePacket extends StylePacket {
        default ServerboundStylePacket sendToServer() {
            //? if <= 1.20.1 {
            /*StylesLegacyNetworking.CHANNEL.sendToServer(this);
            *///?} else {
            NetworkManager.sendToServer(this);
            //?}
            return this;
        }
    }

    interface ClientboundStylePacket extends StylePacket {
        default ClientboundStylePacket sendToPlayer(ServerPlayer player) {
            //? if <= 1.20.1 {
            /*StylesLegacyNetworking.CHANNEL.sendToPlayer(player, this);
            *///?} else {
            NetworkManager.sendToPlayer(player, this);
            //?}
            return this;
        }

        default ClientboundStylePacket sendToPlayers(Iterable<ServerPlayer> players) {
            players.forEach(this::sendToPlayer);
            return this;
        }

        default ClientboundStylePacket sendToTrackingPlayers(Entity entity) {
            //? if <= 1.20.1 {
            /*StylesLegacyNetworking.sendToTrackingPlayers(this, entity);
            *///?} elif >= 1.21.1 {
            StylesNetworking.sendToTrackingPlayers(this, entity);
            //?}
            return this;
        }
    }
}
