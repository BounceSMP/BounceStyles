package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.core.StyleRegistry;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import dev.bsmp.bouncestyles.core.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleUnlocksClientbound;
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
}
