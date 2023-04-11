package dev.bsmp.bouncestyles.networking;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.networking.NetworkChannel;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.networking.packets.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class BounceStylesNetwork {
    public static final NetworkChannel CHANNEL = NetworkChannel.create(new Identifier(BounceStyles.modId, "network"));

    public static void initServerbound() {
        CHANNEL.register(EquipStyleServerbound.class, EquipStyleServerbound::encode, EquipStyleServerbound::decode, ServerPacketHandler::handleEquipStyle);
        CHANNEL.register(ToggleArmorVisibilityServerbound.class, ToggleArmorVisibilityServerbound::encode, ToggleArmorVisibilityServerbound::decode, ServerPacketHandler::handleArmorVisibility);
        CHANNEL.register(OpenStyleScreenServerbound.class, OpenStyleScreenServerbound::encode, OpenStyleScreenServerbound::decode, ServerPacketHandler::handleOpenStyleScreen);
    }

    public static void initClientbound() {
        CHANNEL.register(SyncStyleDataClientbound.class, SyncStyleDataClientbound::encode, SyncStyleDataClientbound::decode, ClientPacketHandler::handleSyncStyleData);
        CHANNEL.register(SyncStyleUnlocksClientbound.class, SyncStyleUnlocksClientbound::encode, SyncStyleUnlocksClientbound::decode, ClientPacketHandler::handleSyncStyleUnlocks);
    }

    @ExpectPlatform
    public static void sendToTrackingPlayers(StylePacket.ClientboundStylePacket packet, Entity entity) {
        throw new AssertionError();
    }
}
