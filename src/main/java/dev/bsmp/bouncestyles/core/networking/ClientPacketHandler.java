package dev.bsmp.bouncestyles.core.networking;

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.core.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.data.preset.PresetManager;
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
            StyleData.setEntityData((Player) entity, packet.styleData());
        }
    }

    public static void handleOpenWardrobeUI(OpenWardrobeUIClientbound packet, Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext ctx = contextSupplier.get();
        ctx.queue(() -> handleOpenWardrobeUI(packet));
    }

    public static void handleOpenWardrobeUI(OpenWardrobeUIClientbound packet) {
        PresetManager.loadPresets();
        Minecraft.getInstance().setScreen(new WardrobeScreen(packet.unlocks()));
    }
}
