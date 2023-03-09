package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.data.StyleData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SyncStyleDataS2C {
    public static final Identifier ID = new Identifier(BounceStyles.modId, "sync_data_s2c");

    public static void handle(MinecraftClient minecraft, ClientPlayNetworkHandler clientPacketListener, PacketByteBuf buf, PacketSender packetSender) {
        int entityId = buf.readInt();
        StyleData styleData = StyleData.equippedFromNBT(buf.readNbt());

        minecraft.execute(() -> {
            Entity entity = minecraft.world.getEntityById(entityId);
            if(entity instanceof PlayerEntity)
                StyleData.setPlayerData((PlayerEntity) entity, styleData);
        });
    }

    public static void sendToPlayer(ServerPlayerEntity player, int entityId, StyleData styleData) {
        ServerPlayNetworking.send(player, ID, toBuf(entityId, styleData));
    }

    private static PacketByteBuf toBuf(int entityId, StyleData styleData) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(entityId);
        buf.writeNbt(StyleData.equippedToNBT(styleData));
        return buf;
    }

    public static void startTracking(Entity entity, ServerPlayerEntity player) {
        if(entity instanceof PlayerEntity)
            sendToPlayer(player, entity.getId(), StyleData.getPlayerData((PlayerEntity)entity));
    }

    public static void playerJoin(ServerPlayNetworkHandler serverGamePacketListener, PacketSender packetSender, MinecraftServer server) {
        ServerPlayerEntity player = serverGamePacketListener.getPlayer();
        sendToPlayer(player, player.getId(), StyleData.getPlayerData(player));
    }
}
