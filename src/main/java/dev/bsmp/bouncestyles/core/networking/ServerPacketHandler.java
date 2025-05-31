package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.ToggleArmorVisibilityServerbound;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class ServerPacketHandler {
    public static void handleEquipStyle(EquipStyleServerbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) ctx.getPlayer();

        ctx.queue(() -> {
            StyleData styleData = StyleData.getOrCreateStyleData(player);

            packet.styleMap().forEach((category, pair) -> {
                Style style = null;
                int textureId = -1;

                if (pair.isPresent()) {
                    style = BounceStylesRegistries.getStyle(pair.get().getFirst()).orElse(null);
                    textureId = pair.get().getSecond();
                }

                styleData.setStyleForSlot(category, style, textureId);
            });

            SyncStyleDataClientbound packetOut = new SyncStyleDataClientbound(player.getId(), styleData);
            packetOut.sendToPlayer(player);
            packetOut.sendToTrackingPlayers(player);
        });
    }

    public static void handleArmorVisibility(ToggleArmorVisibilityServerbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) ctx.getPlayer();

        ctx.queue(() -> {
            StyleData styleData = StyleData.getOrCreateStyleData(player);
            styleData.toggleArmorVisibility(packet.index());

            SyncStyleDataClientbound outPacket = new SyncStyleDataClientbound(player.getId(), styleData);
            outPacket.sendToPlayer(player);
            outPacket.sendToTrackingPlayers(player);
        });
    }

    public static void handleOpenStyleScreen(OpenStyleScreenServerbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) ctx.getPlayer();

        ctx.queue(() -> new OpenWardrobeUIClientbound(StyleData.getOrCreateStyleData(player).getUnlocks()).sendToPlayer(player));
    }
}
