package dev.bsmp.bouncestyles.core.networking;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface StylePacket /*? if neoforge {*/ extends net.minecraft.network.protocol.common.custom.CustomPacketPayload /*?}*/ {
    interface ServerboundStylePacket extends StylePacket {
        default void sendToServer() {
            //? if neoforge {
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(this);
            //? else {
//            BounceStylesNetwork.CHANNEL.sendToServer(this);
            //? }
        }
    }

    interface ClientboundStylePacket extends StylePacket {
        default void sendToPlayer(ServerPlayer player) {
            //? if neoforge {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, this);
            //? } else {
            //BounceStylesNetwork.CHANNEL.sendToPlayer(player, this);
            //? }
        }

        default void sendToPlayers(Iterable<ServerPlayer> players) {
            //? if neoforge {
            players.forEach(this::sendToPlayer);
            //? } else {
            //BounceStylesNetwork.CHANNEL.sendToPlayers(players, this);
            //? }
        }

        default void sendToTrackingPlayers(Entity entity) {
            BounceStylesNetwork.sendToTrackingPlayers(this, entity);
        }
    }
}
