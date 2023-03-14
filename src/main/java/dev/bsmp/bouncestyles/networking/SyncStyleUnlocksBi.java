package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.client.BounceStylesClient;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.data.StyleData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class SyncStyleUnlocksBi {
    StyleData styleData;

    public SyncStyleUnlocksBi() {}
    public SyncStyleUnlocksBi(StyleData styleData) {
        this.styleData = styleData;
    }

    public static void encode(SyncStyleUnlocksBi msg, PacketByteBuf buf) {
        if(msg.styleData != null)
            buf.writeNbt(StyleData.toNBT(msg.styleData));
    }

    public static SyncStyleUnlocksBi decode(PacketByteBuf buf) {
        if(buf.readableBytes() > 0)
            return new SyncStyleUnlocksBi(StyleData.fromNBT(buf.readNbt()));
        else
            return new SyncStyleUnlocksBi();
    }

    public static void handle(SyncStyleUnlocksBi msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if(context.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                BounceStylesClient.openWardrobeScreen(msg.styleData);
            }
            else
                sendToPlayer(context.getSender(), StyleData.getPlayerData(context.getSender()));
        });
        context.setPacketHandled(true);
    }

    public static void sendToPlayer(ServerPlayerEntity player, StyleData styleData) {
        BounceStylesNetwork.NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new SyncStyleUnlocksBi(styleData));
    }
}
