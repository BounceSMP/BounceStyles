package dev.bsmp.bouncestyles.networking;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public interface StylePacket {
    interface ServerboundStylePacket extends StylePacket {
        default void sendToServer() {
            BounceStylesNetwork.CHANNEL.sendToServer(this);
        }
    }

    interface ClientboundStylePacket extends StylePacket {
        default void sendToPlayer(ServerPlayerEntity player) {
            BounceStylesNetwork.CHANNEL.sendToPlayer(player, this);
        }

        default void sendToPlayers(Iterable<ServerPlayerEntity> players) {
            BounceStylesNetwork.CHANNEL.sendToPlayers(players, this);
        }

        default void sendToTrackingPlayers(Entity entity) {
            BounceStylesNetwork.sendToTrackingPlayers(this, entity);
        }
    }
}
