package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ToggleArmorVisibilityC2S {
    public static final Identifier ID = new Identifier(BounceStyles.modId, "toggle_armor");

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler serverGamePacketListener, PacketByteBuf buf, PacketSender packetSender) {
        server.execute(() -> {
            StyleData styleData = StyleData.getPlayerData(player);
            styleData.toggleArmorVisibility();

            SyncStyleDataS2C.sendToPlayer(player, player.getId(), styleData);
            for(ServerPlayerEntity trackingPlayer : PlayerLookup.tracking(player)) {
                SyncStyleDataS2C.sendToPlayer(trackingPlayer, player.getId(), styleData);
            }
        });
    }

    public static void sendToServer() {
        ClientPlayNetworking.send(ID, PacketByteBufs.create());
    }

}
