package dev.bsmp.bouncestyles.networking;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.StyleRegistry;
import dev.bsmp.bouncestyles.client.BounceStylesClient;
import dev.bsmp.bouncestyles.client.screen.MissingWarningScreen;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.networking.clientbound.SyncRegisteredStylesClientbound;
import dev.bsmp.bouncestyles.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.networking.clientbound.SyncStyleUnlocksClientbound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ClientPacketHandler {
    public static void handleSyncStyleData(SyncStyleDataClientbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();

        ctx.queue(() -> {
            Entity entity = MinecraftClient.getInstance().player.getWorld().getEntityById(packet.entityId());
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

    public static void handleSyncRegisteredStyles(SyncRegisteredStylesClientbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        Set<Identifier> registeredStyles = packet.identifiers();

        ctx.queue(() -> {
            List<Identifier> missingIds = new ArrayList<>();
            for(Identifier serverId : registeredStyles) {
                if(!StyleRegistry.idExists(serverId)) missingIds.add(serverId);
            }

            int hash = missingIds.hashCode();

            if(!missingIds.isEmpty() && BounceStylesClient.shouldShowWarningForServer(hash)) {
                File logFile = BounceStylesClient.writeMissingStyleLog(missingIds);
                MinecraftClient.getInstance().setScreen(new MissingWarningScreen(logFile, hash));
            }
        });
    }
}
