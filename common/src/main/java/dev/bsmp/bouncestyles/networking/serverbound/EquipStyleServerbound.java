package dev.bsmp.bouncestyles.networking.serverbound;

import dev.bsmp.bouncestyles.StyleRegistry;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.networking.StylePacket;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public record EquipStyleServerbound(StyleRegistry.Category category, @Nullable Style style) implements StylePacket.ServerboundStylePacket {
    public void encode(PacketByteBuf buf) {
        buf.writeEnumConstant(this.category);
        if(this.style != null)
            buf.writeIdentifier(style.styleId);
    }
    public static EquipStyleServerbound decode(PacketByteBuf buf) {
        return new EquipStyleServerbound(
                buf.readEnumConstant(StyleRegistry.Category.class),
                buf.readableBytes() > 0 ? StyleRegistry.getStyle(buf.readIdentifier()) : null
        );
    }
}
