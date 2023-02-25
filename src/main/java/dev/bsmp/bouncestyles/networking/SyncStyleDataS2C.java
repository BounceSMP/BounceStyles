package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.data.PlayerStyleData;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class SyncStyleDataS2C {
    public static final ResourceLocation ID = new ResourceLocation(BounceStyles.modId, "sync_data_s2c");

    public static void handle(Minecraft minecraft, ClientPacketListener clientPacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
        int entityId = buf.readInt();
        PlayerStyleData styleData = PlayerStyleData.equippedFromNBT(buf.readNbt());

        minecraft.execute(() -> {
            Entity entity = minecraft.level.getEntity(entityId);
            if(entity instanceof Player)
                PlayerStyleData.setPlayerData((Player) entity, styleData);
        });
    }

    public static void sendToPlayer(ServerPlayer player, int entityId, PlayerStyleData styleData) {
        ServerPlayNetworking.send(player, ID, toBuf(entityId, styleData));
    }

    private static FriendlyByteBuf toBuf(int entityId, PlayerStyleData styleData) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        buf.writeNbt(PlayerStyleData.equippedToNBT(styleData));
        return buf;
    }

    public static void startTracking(Entity entity, ServerPlayer player) {
        if(entity instanceof Player)
            sendToPlayer(player, entity.getId(), PlayerStyleData.getPlayerData((Player)entity));
    }

    public static void playerJoin(ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, MinecraftServer server) {
        ServerPlayer player = serverGamePacketListener.getPlayer();
        sendToPlayer(player, player.getId(), PlayerStyleData.getPlayerData(player));
    }
}
