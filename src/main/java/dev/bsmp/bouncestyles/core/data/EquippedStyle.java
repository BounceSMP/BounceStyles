package dev.bsmp.bouncestyles.core.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class EquippedStyle {
    private @Nullable Identifier styleId = null;
    private int variant = -1;

    public EquippedStyle() {}

    public EquippedStyle(@Nullable Identifier styleId) {
        this.styleId = styleId;
    }

    public EquippedStyle(@Nullable Identifier styleId, int variant) {
        this.styleId = styleId;
        this.variant = variant;
    }

    public Optional<Identifier> getStyleId() {
        return Optional.ofNullable(styleId);
    }

    public void setStyleId(@Nullable Identifier styleId) {
        this.styleId = styleId;
        if (styleId == null) this.variant = -1;
    }

    public int getVariant() {
        return variant;
    }

    public void setVariant(int variant) {
        this.variant = variant;
    }

    public static EquippedStyle decode(Optional<Identifier> styleId, int variant) {
        return new EquippedStyle(styleId.orElse(null), variant);
    }

    public static final Codec<EquippedStyle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("style_id").forGetter(EquippedStyle::getStyleId),
            Codec.INT.optionalFieldOf("variant", -1).forGetter(EquippedStyle::getVariant)
    ).apply(instance, EquippedStyle::decode));
}
