package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.ToggleArmorVisibilityServerbound;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.function.Supplier;

public class ServerPacketHandler {
    public static void handleEquipStyle(EquipStyleServerbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ServerPlayer player = (ServerPlayer) ctx.getPlayer();

        ctx.queue(() -> {
            StyleData styleData = StyleData.getOrCreateStyleData(player);
            if(packet.styleId().isEmpty() || styleData.hasStyleUnlocked(packet.styleId().get()) || (player.isCreative() && player.hasPermissions(2))) {
                Optional<Style> style = packet.styleId().map(BounceStylesRegistries::getStyle).orElse(Optional.empty());
                switch (packet.category()) {
                    case Head -> styleData.setHeadStyle(style.orElse(null));
                    case Body -> styleData.setBodyStyle(style.orElse(null));
                    case Legs -> styleData.setLegStyle(style.orElse(null));
                    case Feet -> styleData.setFeetStyle(style.orElse(null));
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

        ctx.queue(() -> new OpenWardrobeUIClientbound(StyleData.getOrCreateStyleData(player).getUnlocks()).sendToPlayer(player));
    }
}
