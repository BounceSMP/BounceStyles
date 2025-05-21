package dev.bsmp.bouncestyles.core.networking.serverbound;

import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.data.Style;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public record EquipStyleServerbound(BounceStylesRegistries.Category category, @Nullable Style style) implements StylePacket.ServerboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(this.category);
        if(this.style != null)
            buf.writeResourceLocation(style.getStyleId());
    }
    public static EquipStyleServerbound decode(FriendlyByteBuf buf) {
        return new EquipStyleServerbound(
                buf.readEnum(BounceStylesRegistries.Category.class),
                buf.readableBytes() > 0 ? BounceStylesRegistries.getStyle(buf.readResourceLocation()) : null
        );
    }
}
