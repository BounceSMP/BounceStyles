package dev.bsmp.bouncestyles.networking;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.networking.packets.OpenStyleScreenServerbound;
import dev.bsmp.bouncestyles.networking.packets.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.networking.packets.SyncStyleUnlocksClientbound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Supplier;

public class ClientPacketHandler {
    public static void handleSyncStyleData(SyncStyleDataClientbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();

        ctx.queue(() -> {
            Entity entity = MinecraftClient.getInstance().player.world.getEntityById(packet.entityId());
            if(entity instanceof PlayerEntity) {
                StyleData.setPlayerData((PlayerEntity) entity, packet.styleData());
            }
        });
    }

    public static void handleSyncStyleUnlocks(SyncStyleUnlocksClientbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        StyleData styleData = packet.styleData();

        ctx.queue(() -> {
            StyleData.setPlayerData(ctx.getPlayer(), styleData);
            MinecraftClient.getInstance().setScreen(new WardrobeScreen(styleData.getUnlocks()));
        });
    }
}
