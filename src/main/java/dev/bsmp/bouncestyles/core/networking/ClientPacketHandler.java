package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.StyleLoader;
import dev.bsmp.bouncestyles.core.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.core.data.StyleData;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class ClientPacketHandler {
    public static void handleSyncStyleData(SyncStyleDataClientbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ctx.queue(() -> handleSyncStyleData(packet));
    }

    public static void handleSyncStyleData(SyncStyleDataClientbound packet) {
        Entity entity = Minecraft.getInstance().player.level().getEntity(packet.entityId());
        if(entity instanceof Player) {
            StyleData.setPlayerData((Player) entity, packet.styleData());
        }
    }

    public static void handleOpenWardrobeUI(OpenWardrobeUIClientbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ctx.queue(() -> handleOpenWardrobeUI(ctx.getPlayer(), packet));
    }

    public static void handleOpenWardrobeUI(Player player, OpenWardrobeUIClientbound packet) {
        try {
            StyleLoader.loadPresets();
        }
        catch (Exception e) {
            BounceStyles.LOGGER.error("Exception Occurred reading Presets file", e);
        }
        StyleData styleData = StyleData.getOrCreateStyleData(player);
        styleData.setUnlocks(packet.unlocks());
        Minecraft.getInstance().setScreen(new WardrobeScreen(styleData.getUnlocks()));
    }
}
