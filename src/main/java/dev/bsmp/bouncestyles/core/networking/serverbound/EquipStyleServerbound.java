package dev.bsmp.bouncestyles.core.networking.serverbound;

import com.mojang.serialization.Codec;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.api.data.EquippedStyle;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

import java.util.Map;

public record EquipStyleServerbound(Map<Category, EquippedStyle> styleMap) implements StylePacket.ServerboundStylePacket {
    public EquipStyleServerbound(Category category) {
        this(Map.of(category, new EquippedStyle()));
    }

    public EquipStyleServerbound(Category category, Identifier styleId) {
        this(category, styleId, -1);
    }

    public EquipStyleServerbound(Category category, Identifier styleId, int textureId) {
        this(Map.of(category, new EquippedStyle(BounceStylesRegistries.getStyle(styleId).orElse(null), textureId)));
    }

    //? if <= 1.20.1 {
    /*private static final Codec<Map<Category, Optional<Pair<Identifier, Integer>>>> CODEC = Codec.unboundedMap(Category.CODEC, Codec.optionalField("value", Codec.pair(
            Identifier.CODEC.fieldOf("style_id").codec(),
            Codec.INT.optionalFieldOf("texture_variant", -1).codec()
    )).codec());
    *///?} elif >= 1.21.1 {
    public static final Type<EquipStyleServerbound> TYPE = new net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<>(BounceStyles.id("serverbound_equip_style"));

    @Override
    public Type<? extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> type() {
        return TYPE;
    }

    private static final Codec<Map<Category, EquippedStyle>> CODEC = Codec.unboundedMap(Category.CODEC, EquippedStyle.CODEC);

    public static final net.minecraft.network.codec.StreamCodec<ByteBuf, EquipStyleServerbound> STREAM_CODEC = net.minecraft.network.codec.StreamCodec.composite(
            net.minecraft.network.codec.ByteBufCodecs.fromCodec(CODEC),
            EquipStyleServerbound::styleMap,
            EquipStyleServerbound::new
    );
    //?}
}
