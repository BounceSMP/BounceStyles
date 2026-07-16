package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.data.preset.ServerPresets;
import dev.bsmp.bouncestyles.core.data.unlocks.UnlockManager;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncPresetsClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.UpdatePresetServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.RequestPresetsServerbound;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ServerPacketHandler {
    public static void handleEquipStyle(NetworkManager.PacketContext context, EquipStyleServerbound packet) {
        var player = (ServerPlayer) context.getPlayer();
        StyleData styleData = StyleData.getEntityData(player);

        packet.styleMap().forEach((category, equippedStyle) -> {
            if (equippedStyle.getStyleId().isEmpty() || (!UnlockManager.requiresUnlocks(player) || UnlockManager.hasUnlocked(player, equippedStyle.getStyleId().get())))
                styleData.equipStyle(category, equippedStyle);
        });

        new SyncStyleDataClientbound(player.getId(), styleData)
                .sendToPlayer(player)
                .sendToTrackingPlayers(player);
    }

    public static void handleOpenStyleScreen(NetworkManager.PacketContext context, OpenStyleScreenServerbound packet) {
        var player = (ServerPlayer) context.getPlayer();
        var unlocks = UnlockManager.requiresUnlocks(player) ? UnlockManager.readUnlockData(player).orElse(Set.of()).stream().toList() : null;
        new OpenWardrobeUIClientbound(Optional.ofNullable(unlocks)).sendToPlayer(player);
    }

    public static void handleRequestPresets(NetworkManager.PacketContext context, RequestPresetsServerbound packet) {
        var player = (ServerPlayer) context.getPlayer();
        var server = player.level().getServer();
        new SyncPresetsClientbound(
                ServerPresets.getGlobalPresets(server).orElse(Map.of()),
                ServerPresets.getPlayerPresets(player).orElse(Map.of())
        ).sendToPlayer(player);
    }

    public static void handleCreatePreset(NetworkManager.PacketContext context, UpdatePresetServerbound packet) {
        var player = (ServerPlayer) context.getPlayer();
        ServerPresets.createPlayerPreset(player, packet.presetName(), packet.preset().orElse(null));
    }
}
