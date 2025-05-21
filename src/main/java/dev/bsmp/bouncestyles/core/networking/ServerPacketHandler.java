package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleUnlocksClientbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.ToggleArmorVisibilityServerbound;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;

public class ServerPacketHandler {
    public static void handleEquipStyle(EquipStyleServerbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) ctx.getPlayer();

        ctx.queue(() -> {
            StyleData styleData = StyleData.getOrCreateStyleData(player);
            if(packet.style() == null || styleData.hasStyleUnlocked(packet.style()) || (player.isCreative() && player.hasPermissions(2))) {
                switch (packet.category()) {
                    case Head -> styleData.setHeadStyle(packet.style());
                    case Body -> styleData.setBodyStyle(packet.style());
                    case Legs -> styleData.setLegStyle(packet.style());
                    case Feet -> styleData.setFeetStyle(packet.style());
                }

                SyncStyleDataClientbound packetOut = new SyncStyleDataClientbound(player.getId(), styleData);
                packetOut.sendToPlayer(player);
                packetOut.sendToTrackingPlayers(player);
            }
        });
    }

    public static void handleArmorVisibility(ToggleArmorVisibilityServerbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) ctx.getPlayer();

        ctx.queue(() -> {
            StyleData styleData = StyleData.getOrCreateStyleData(player);
            styleData.toggleArmorVisibility();

            SyncStyleDataClientbound outPacket = new SyncStyleDataClientbound(player.getId(), styleData);
            outPacket.sendToPlayer(player);
            outPacket.sendToTrackingPlayers(player);
        });
    }

    public static void handleOpenStyleScreen(OpenStyleScreenServerbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) ctx.getPlayer();

        ctx.queue(() -> new SyncStyleUnlocksClientbound(StyleData.getOrCreateStyleData(player)).sendToPlayer(player));
    }
}
