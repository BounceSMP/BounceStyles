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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class ClientPacketHandler {
    public static void handleSyncStyleData(SyncStyleDataClientbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();

        ctx.queue(() -> {
            Entity entity = Minecraft.getInstance().player.level().getEntity(packet.entityId());
            if(entity instanceof Player) {
                StyleData.setPlayerData((Player) entity, packet.styleData());
            }
        });
    }

    public static void handleSyncStyleUnlocks(SyncStyleUnlocksClientbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        StyleData styleData = packet.styleData();

        ctx.queue(() -> {
            StyleData.setPlayerData(ctx.getPlayer(), styleData);
            Minecraft.getInstance().setScreen(new WardrobeScreen(styleData.getUnlocks()));
        });
    }

    public static void handleSyncRegisteredStyles(SyncRegisteredStylesClientbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        Set<ResourceLocation> registeredStyles = packet.identifiers();

        ctx.queue(() -> {
            List<ResourceLocation> missingIds = new ArrayList<>();
            for(ResourceLocation serverId : registeredStyles) {
                if(!StyleRegistry.idExists(serverId)) missingIds.add(serverId);
            }

            int hash = missingIds.hashCode();

            if(!missingIds.isEmpty() && BounceStylesClient.shouldShowWarningForServer(hash)) {
                File logFile = BounceStylesClient.writeMissingStyleLog(missingIds);
                Minecraft.getInstance().setScreen(new MissingWarningScreen(logFile, hash));
            }
        });
    }
}
