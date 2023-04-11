package dev.bsmp.bouncestyles.networking.fabric;

import dev.bsmp.bouncestyles.networking.BounceStylesNetwork;
import dev.bsmp.bouncestyles.networking.packets.StylePacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class BounceStylesNetworkImpl {
    public static void sendToTrackingPlayers(StylePacket.ClientboundStylePacket packet, Entity entity) {
        for(ServerPlayerEntity player : PlayerLookup.tracking(entity)) {
            BounceStylesNetwork.CHANNEL.sendToPlayer(player, packet);
        }
    }
}
