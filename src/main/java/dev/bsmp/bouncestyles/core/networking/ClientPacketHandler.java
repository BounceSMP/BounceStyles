package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.core.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

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

    public static void handleOpenWardrobeUI(OpenWardrobeUIClientbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();

        ctx.queue(() -> {
            StyleData styleData = StyleData.getOrCreateStyleData(ctx.getPlayer());
            styleData.setUnlocks(packet.unlocks());
            Minecraft.getInstance().setScreen(new WardrobeScreen(styleData.getUnlocks()));
        });
    }
}
