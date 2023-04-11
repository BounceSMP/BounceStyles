package dev.bsmp.bouncestyles.networking.forge;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.networking.BounceStylesNetwork;
import dev.bsmp.bouncestyles.networking.packets.StylePacket;
import net.minecraft.entity.Entity;
import net.minecraftforge.network.PacketDistributor;

public class BounceStylesNetworkImpl {
    public static void sendToTrackingPlayers(StylePacket.ClientboundStylePacket packet, Entity entity) {
        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity).send(BounceStylesNetwork.CHANNEL.toPacket(NetworkManager.Side.S2C, packet));
    }
}
