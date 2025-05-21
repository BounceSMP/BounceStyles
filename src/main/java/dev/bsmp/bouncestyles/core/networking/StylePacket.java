package dev.bsmp.bouncestyles.core.networking;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface StylePacket {
    interface ServerboundStylePacket extends StylePacket {
        default void sendToServer() {
            BounceStylesNetwork.CHANNEL.sendToServer(this);
        }
    }

    interface ClientboundStylePacket extends StylePacket {
        default void sendToPlayer(ServerPlayer player) {
            BounceStylesNetwork.CHANNEL.sendToPlayer(player, this);
        }

        default void sendToPlayers(Iterable<ServerPlayer> players) {
            BounceStylesNetwork.CHANNEL.sendToPlayers(players, this);
        }

        default void sendToTrackingPlayers(Entity entity) {
            BounceStylesNetwork.sendToTrackingPlayers(this, entity);
        }
    }
}
