package dev.bsmp.bouncestyles.core.networking.serverbound;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries.Category;
import dev.bsmp.bouncestyles.core.networking.StylePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;

public record EquipStyleServerbound(Map<Category, Optional<Pair<ResourceLocation, Integer>>> styleMap) implements StylePacket.ServerboundStylePacket {
    public EquipStyleServerbound(Category category) {
        this(Map.of(category, Optional.empty()));
    }

    public EquipStyleServerbound(Category category, ResourceLocation styleId) {
        this(category, styleId, -1);
    }

    public EquipStyleServerbound(Category category, ResourceLocation styleId, int textureId) {
        this(Map.of(category, Optional.of(Pair.of(styleId, textureId))));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(CODEC, styleMap);
    }

    public static EquipStyleServerbound decode(FriendlyByteBuf buf) {
        return new EquipStyleServerbound(
                buf.readJsonWithCodec(CODEC)
        );
    }

    private static final Codec<Map<Category, Optional<Pair<ResourceLocation, Integer>>>> CODEC = Codec.unboundedMap(Category.CODEC, Codec.optionalField("value", Codec.pair(
            ResourceLocation.CODEC.fieldOf("style_id").codec(),
            Codec.INT.optionalFieldOf("texture_variant", -1).codec()
    )).codec());
}
