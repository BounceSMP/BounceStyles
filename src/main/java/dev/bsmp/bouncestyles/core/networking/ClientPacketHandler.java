package dev.bsmp.bouncestyles.core.networking;
//~ avatar

import dev.architectury.networking.NetworkManager;
import dev.bsmp.bouncestyles.core.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.data.preset.ClientPresets;
import dev.bsmp.bouncestyles.core.networking.clientbound.OpenWardrobeUIClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncPresetsClientbound;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncStyleDataClientbound;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;

public class ClientPacketHandler {
    public static void handleSyncStyleData(NetworkManager.PacketContext context, SyncStyleDataClientbound packet) {
        Entity entity = Minecraft.getInstance().player.level().getEntity(packet.entityId());
        if(entity instanceof Avatar) {
            StyleData.setEntityData((Avatar) entity, packet.styleData());
        }
    }

    public static void handleOpenWardrobeUI(NetworkManager.PacketContext context, OpenWardrobeUIClientbound packet) {
        //ToDo Request presets from server
        Minecraft.getInstance().setScreen(new WardrobeScreen(packet.unlocks()));
    }

    public static void handleSyncPresets(NetworkManager.PacketContext context, SyncPresetsClientbound packet) {
        ClientPresets.loadPresets(packet.presets());
    }
}
