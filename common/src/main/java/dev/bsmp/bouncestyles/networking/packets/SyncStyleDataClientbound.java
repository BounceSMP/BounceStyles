package dev.bsmp.bouncestyles.networking.packets;

import dev.bsmp.bouncestyles.data.StyleData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public record SyncStyleDataClientbound(int entityId, StyleData styleData) implements StylePacket.ClientboundStylePacket {
    public void encode(PacketByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeNbt(StyleData.equippedToNBT(styleData));
    }

    public static SyncStyleDataClientbound decode(PacketByteBuf buf) {
        return new SyncStyleDataClientbound(
                buf.readInt(),
                StyleData.fromNBT(buf.readNbt())
        );
    }

    public static void startTracking(Entity entity, ServerPlayerEntity player) {
        if(entity instanceof PlayerEntity)
            new SyncStyleDataClientbound(entity.getId(), StyleData.getOrCreateStyleData((PlayerEntity)entity)).sendToPlayer(player);
    }
}
