package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EquipStyleC2S {
    public static final Identifier ID = new Identifier(BounceStyles.modId, "equip_c2s");

    public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler serverGamePacketListener, PacketByteBuf buf, PacketSender packetSender) {
        StyleLoader.Category category = StyleLoader.Category.valueOf(buf.readString());
        Identifier styleID;
        if(buf.readableBytes() > 0)
            styleID = buf.readIdentifier();
        else {
            styleID = null;
        }

        server.execute(() -> {
            StyleData styleData = StyleData.getPlayerData(player);
            Style style = StyleLoader.REGISTRY.get(styleID);
            if(style == null || styleData.hasStyleUnlocked(style) || (player.isCreative() && player.hasPermissionLevel(2))) {
                switch (category) {
                    case Head -> styleData.setHeadStyle(style);
                    case Body -> styleData.setBodyStyle(style);
                    case Legs -> styleData.setLegStyle(style);
                    case Feet -> styleData.setFeetStyle(style);
                }
                SyncStyleDataS2C.sendToPlayer(player, player.getId(), styleData);
            }
        });
    }

    public static void sendToServer(StyleLoader.Category category, Style style) {
        ClientPlayNetworking.send(ID, toBuf(category, style));
    }

    private static PacketByteBuf toBuf(StyleLoader.Category category, Style style) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(category.name());
        if(style != null)
            buf.writeIdentifier(style.styleId);
        return buf;
    }

}
