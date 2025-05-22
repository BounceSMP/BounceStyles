package dev.bsmp.bouncestyles.core.networking.serverbound;

import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.data.Style;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;

public record EquipStyleServerbound(BounceStylesRegistries.Category category, Optional<Style> style) implements StylePacket.ServerboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(this.category);
        if(this.style.isPresent())
            buf.writeResourceLocation(style.get().getStyleId());
    }
    public static EquipStyleServerbound decode(FriendlyByteBuf buf) {
        return new EquipStyleServerbound(
                buf.readEnum(BounceStylesRegistries.Category.class),
                buf.readableBytes() > 0 ? BounceStylesRegistries.getStyle(buf.readResourceLocation()) : Optional.empty()
        );
    }
}
