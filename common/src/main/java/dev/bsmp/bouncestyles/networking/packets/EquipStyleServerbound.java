package dev.bsmp.bouncestyles.networking.packets;

import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.networking.BounceStylesNetwork;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public record EquipStyleServerbound(StyleLoader.Category category, @Nullable Style style) implements StylePacket.ServerboundStylePacket {
    public void encode(PacketByteBuf buf) {
        buf.writeEnumConstant(this.category);
        if(this.style != null)
            buf.writeIdentifier(style.styleId);
    }
    public static EquipStyleServerbound decode(PacketByteBuf buf) {
        return new EquipStyleServerbound(
                buf.readEnumConstant(StyleLoader.Category.class),
                buf.readableBytes() > 0 ? StyleLoader.getStyle(buf.readIdentifier()) : null
        );
    }
}
