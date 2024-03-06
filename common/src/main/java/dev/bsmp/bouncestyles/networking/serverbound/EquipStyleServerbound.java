package dev.bsmp.bouncestyles.networking.serverbound;

import dev.bsmp.bouncestyles.StyleRegistry;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record EquipStyleServerbound(StyleRegistry.Category category, @Nullable Style style) implements StylePacket.ServerboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(this.category);
        if(this.style != null)
            buf.writeResourceLocation(style.styleId);
    }
    public static EquipStyleServerbound decode(FriendlyByteBuf buf) {
        return new EquipStyleServerbound(
                buf.readEnum(StyleRegistry.Category.class),
                buf.readableBytes() > 0 ? StyleRegistry.getStyle(buf.readResourceLocation()) : null
        );
    }
}
