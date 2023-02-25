package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.GarmentLoader;
import dev.bsmp.bouncestyles.data.Garment;
import dev.bsmp.bouncestyles.data.PlayerStyleData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class EquipStyleC2S {
    public static final ResourceLocation ID = new ResourceLocation(BounceStyles.modId, "equip_c2s");

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl serverGamePacketListener, FriendlyByteBuf buf, PacketSender packetSender) {
        GarmentLoader.Category category = GarmentLoader.Category.valueOf(buf.readUtf());
        ResourceLocation garmentID;
        if(buf.readableBytes() > 0)
            garmentID = buf.readResourceLocation();
        else {
            garmentID = null;
        }

        server.execute(() -> {
            PlayerStyleData styleData = PlayerStyleData.getPlayerData(player);
            Garment garment = category.entryList.get(garmentID);
            if(garment == null || styleData.hasGarmentUnlocked(garment) || (player.isCreative() && player.hasPermissions(2))) {
                switch (category) {
                    case Head -> styleData.setHeadGarment(garment);
                    case Body -> styleData.setBodyGarment(garment);
                    case Legs -> styleData.setLegGarment(garment);
                    case Feet -> styleData.setFeetGarment(garment);
                }
                SyncStyleDataS2C.sendToPlayer(player, player.getId(), styleData);
            }
        });
    }

    public static void sendToServer(GarmentLoader.Category category, Garment garment) {
        ClientPlayNetworking.send(ID, toBuf(category, garment));
    }

    private static FriendlyByteBuf toBuf(GarmentLoader.Category category, Garment garment) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(category.name());
        if(garment != null)
            buf.writeResourceLocation(garment.garmentId);
        return buf;
    }

}
