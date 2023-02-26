package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.data.Style;
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
        StyleLoader.Category category = StyleLoader.Category.valueOf(buf.readUtf());
        ResourceLocation styleID;
        if(buf.readableBytes() > 0)
            styleID = buf.readResourceLocation();
        else {
            styleID = null;
        }

        server.execute(() -> {
            PlayerStyleData styleData = PlayerStyleData.getPlayerData(player);
            Style style = StyleLoader.REGISTRY.get(styleID);
            if(style == null || styleData.hasStyleUnlocked(style) || (player.isCreative() && player.hasPermissions(2))) {
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

    private static FriendlyByteBuf toBuf(StyleLoader.Category category, Style style) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUtf(category.name());
        if(style != null)
            buf.writeResourceLocation(style.styleId);
        return buf;
    }

}
