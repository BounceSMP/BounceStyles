package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.data.StyleData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncStyleUnlocksBi {
    public static final Identifier ID_C2S = new Identifier(BounceStyles.modId, "sync_unlocks_c2s");
    public static final Identifier ID_S2C = new Identifier(BounceStyles.modId, "sync_unlocks_s2c");

    public static void handleClient(MinecraftClient minecraft, ClientPlayNetworkHandler clientPacketListener, PacketByteBuf buf, PacketSender packetSender) {
        StyleData styleData = StyleData.fromNBT(buf.readNbt());

        minecraft.execute(() -> {
            StyleData.setPlayerData(minecraft.player, styleData);
            minecraft.setScreen(new WardrobeScreen(styleData.getUnlocks()));
        });
    }

    public static void handleServer(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler serverGamePacketListener, PacketByteBuf buf, PacketSender packetSender) {
        server.execute(() -> sendToPlayer(player, StyleData.getPlayerData(player)));
    }

    public static void sendToPlayer(ServerPlayerEntity player, StyleData styleData) {
        ServerPlayNetworking.send(player, ID_S2C, toClientBuf(styleData));
    }

    public static void sendToServer() {
        ClientPlayNetworking.send(ID_C2S, PacketByteBufs.create());
    }

    private static PacketByteBuf toClientBuf(StyleData styleData) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(StyleData.toNBT(styleData));
        return buf;
    }

}
