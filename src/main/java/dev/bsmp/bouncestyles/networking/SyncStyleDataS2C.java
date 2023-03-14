package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.client.BounceStylesClient;
import dev.bsmp.bouncestyles.data.StyleData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncStyleDataS2C {
    int entityId;
    StyleData styleData;

    public SyncStyleDataS2C(int entityId, StyleData styleData) {
        this.entityId = entityId;
        this.styleData = styleData;
    }

    public static void encode(SyncStyleDataS2C msg, PacketByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeNbt(StyleData.equippedToNBT(msg.styleData));
    }

    public static SyncStyleDataS2C decode(PacketByteBuf buf) {
        int entityId = buf.readInt();
        StyleData styleData = StyleData.equippedFromNBT(buf.readNbt());
        return new SyncStyleDataS2C(entityId, styleData);
    }

    public static void handle(SyncStyleDataS2C msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> BounceStylesClient.syncStyle(msg.entityId, msg.styleData));
        contextSupplier.get().setPacketHandled(true);
    }

    public static void sendToPlayer(ServerPlayerEntity player, int entityId, StyleData styleData) {
        BounceStylesNetwork.NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SyncStyleDataS2C(entityId, styleData));
    }

    public static void startTracking(Entity entity, ServerPlayerEntity player) {
        if(entity instanceof PlayerEntity)
            sendToPlayer(player, entity.getId(), StyleData.getPlayerData((PlayerEntity)entity));
    }

    public static void playerJoin(ServerPlayerEntity player) {
        sendToPlayer(player, player.getId(), StyleData.getPlayerData(player));
    }
}
