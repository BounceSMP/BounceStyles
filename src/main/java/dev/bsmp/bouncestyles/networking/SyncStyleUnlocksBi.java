package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.data.PlayerStyleData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class SyncStyleUnlocksBi {
    public static final ResourceLocation ID_C2S = new ResourceLocation(BounceStyles.modId, "sync_unlocks_c2s");
    public static final ResourceLocation ID_S2C = new ResourceLocation(BounceStyles.modId, "sync_unlocks_s2c");

    public static void handleClient(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
        PlayerStyleData styleData = PlayerStyleData.fromNBT(buf.readNbt());

        minecraft.execute(() -> {
            PlayerStyleData.setPlayerData(minecraft.player, styleData);
            minecraft.setScreen(new WardrobeScreen(styleData.getUnlocks()));
        });
    }

    public static void handleServer(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl serverGamePacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
        server.execute(() -> sendToPlayer(player, PlayerStyleData.getPlayerData(player)));
    }

    public static void sendToPlayer(ServerPlayer player, PlayerStyleData styleData) {
        ServerPlayNetworking.send(player, ID_S2C, toClientBuf(styleData));
    }

    public static void sendToServer() {
        ClientPlayNetworking.send(ID_C2S, PacketByteBufs.create());
    }

    private static FriendlyByteBuf toClientBuf(PlayerStyleData styleData) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(PlayerStyleData.toNBT(styleData));
        return buf;
    }

}
