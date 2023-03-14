package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.data.StyleData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class ToggleArmorVisibilityC2S {

    public static void encode(ToggleArmorVisibilityC2S msg, PacketByteBuf buf) {}

    public static ToggleArmorVisibilityC2S decode(PacketByteBuf buf) {
        return new ToggleArmorVisibilityC2S();
    }

    public static void handle(ToggleArmorVisibilityC2S msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayerEntity player = contextSupplier.get().getSender();
            StyleData styleData = StyleData.getPlayerData(player);
            styleData.toggleArmorVisibility();

            SyncStyleDataS2C.sendToPlayer(player, player.getId(), styleData);
            BounceStylesNetwork.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncStyleDataS2C(player.getId(), styleData));
        });
        contextSupplier.get().setPacketHandled(true);
    }

}
