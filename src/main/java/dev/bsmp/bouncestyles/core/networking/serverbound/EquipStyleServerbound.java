package dev.bsmp.bouncestyles.core.networking.serverbound;

import dev.bsmp.bouncestyles.core.BounceStylesRegistries.Category;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record EquipStyleServerbound(Category category, Optional<ResourceLocation> styleId) implements StylePacket.ServerboundStylePacket {
    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(this.category);
        if(this.styleId.isPresent())
            buf.writeResourceLocation(styleId.get());
    }

    public static EquipStyleServerbound decode(FriendlyByteBuf buf) {
        return new EquipStyleServerbound(
                buf.readEnum(Category.class),
                buf.readableBytes() > 0 ? Optional.of(buf.readResourceLocation()) : Optional.empty()
        );
    }
}
