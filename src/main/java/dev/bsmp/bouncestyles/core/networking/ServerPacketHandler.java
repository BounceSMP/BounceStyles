package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.data.unlocks.UnlockManager;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;
import java.util.function.Supplier;

public class ServerPacketHandler {
    public static void handleEquipStyle(EquipStyleServerbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) ctx.getPlayer();

        ctx.queue(() -> handleEquipStyle(player, packet));
    }

    public static void handleEquipStyle(ServerPlayer player, EquipStyleServerbound packet) {
        StyleData styleData = StyleData.getEntityData(player);

        packet.styleMap().forEach((category, equippedStyle) -> {
            if (equippedStyle.getStyleId().isEmpty() || UnlockManager.hasUnlocked(player, equippedStyle.getStyleId().get()))
                styleData.equipStyle(category, equippedStyle);
        });

        SyncStyleDataClientbound packetOut = new SyncStyleDataClientbound(player.getId(), styleData);
        packetOut.sendToPlayer(player);
        packetOut.sendToTrackingPlayers(player);
    }

    public static void handleOpenStyleScreen(OpenStyleScreenServerbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) ctx.getPlayer();

        ctx.queue(() -> new OpenWardrobeUIClientbound(UnlockManager.readUnlockData(player).orElse(Set.of()).stream().toList()).sendToPlayer(player));
    }
}
