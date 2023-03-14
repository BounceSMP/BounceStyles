package dev.bsmp.bouncestyles.networking;

import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class EquipStyleC2S {
    StyleLoader.Category category;
    Style style;

    public EquipStyleC2S(StyleLoader.Category category, Style style) {
        this.category = category;
        this.style = style;
    }

    public static void encode(EquipStyleC2S msg, PacketByteBuf buf) {
        buf.writeEnumConstant(msg.category);
        if(msg.style != null)
            buf.writeIdentifier(msg.style.styleId);
    }

    public static EquipStyleC2S decode(PacketByteBuf buf) {
        StyleLoader.Category category = buf.readEnumConstant(StyleLoader.Category.class);
        Identifier id = buf.readableBytes() > 0 ? buf.readIdentifier() : null;
        return new EquipStyleC2S(category, StyleLoader.getStyle(id));
    }


    public static void handle(EquipStyleC2S msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayerEntity player = contextSupplier.get().getSender();
            StyleData styleData = StyleData.getPlayerData(player);
            if(msg.style == null || styleData.hasStyleUnlocked(msg.style) || (player.isCreative() && player.hasPermissionLevel(2))) {
                switch (msg.category) {
                    case Head -> styleData.setHeadStyle(msg.style);
                    case Body -> styleData.setBodyStyle(msg.style);
                    case Legs -> styleData.setLegStyle(msg.style);
                    case Feet -> styleData.setFeetStyle(msg.style);
                }

                SyncStyleDataS2C.sendToPlayer(player, player.getId(), styleData);
                BounceStylesNetwork.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SyncStyleDataS2C(player.getId(), styleData));
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
