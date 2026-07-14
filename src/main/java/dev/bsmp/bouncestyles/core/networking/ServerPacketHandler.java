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

public class ServerPacketHandler {
    public static void handleEquipStyle(NetworkManager.PacketContext context, EquipStyleServerbound packet) {
        var player = (ServerPlayer) context.getPlayer();
        StyleData styleData = StyleData.getEntityData(player);

        packet.styleMap().forEach((category, equippedStyle) -> {
            if (equippedStyle.getStyleId().isEmpty() || UnlockManager.hasUnlocked(player, equippedStyle.getStyleId().get()))
                styleData.equipStyle(category, equippedStyle);
        });

        new SyncStyleDataClientbound(player.getId(), styleData)
                .sendToPlayer(player)
                .sendToTrackingPlayers(player);
    }

    public static void handleOpenStyleScreen(NetworkManager.PacketContext context, OpenStyleScreenServerbound packet) {
        var player = (ServerPlayer) context.getPlayer();
        var unlocks = UnlockManager.readUnlockData(player).orElse(Set.of()).stream().toList();
        new OpenWardrobeUIClientbound(unlocks).sendToPlayer(player);
    }
}
