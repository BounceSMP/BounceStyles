package dev.bsmp.bouncestyles.core.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.data.style.Style;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class EquippedStyle {
    private @Nullable Style style = null;
    private int variant = -1;

    public EquippedStyle() {}

    public EquippedStyle(@Nullable Style style) {
        this.style = style;
    }

    public EquippedStyle(@Nullable Style style, int variant) {
        this.style = style;
        this.variant = variant;
    }

    public Optional<Style> getStyle() {
        return Optional.ofNullable(style);
    }

    public Optional<Identifier> getStyleId() {
        return getStyle().map(Style::getStyleId);
    }

    public void setStyle(@Nullable Style style) {
        this.style = style;
        if (style == null) this.variant = -1;
    }

    public int getVariant() {
        return variant;
    }

    public Optional<Identifier> getTextureId() {
        return this.getStyle().map(s -> s.getTextureId(this.getVariant()));
    }

    public void setVariant(int variant) {
        this.variant = variant;
    }

    public static EquippedStyle decode(Optional<Identifier> styleId, int variant) {
        var style = styleId.flatMap(BounceStylesRegistries::getStyle).orElse(null);
        return new EquippedStyle(style, variant);
    }

    public static final Codec<EquippedStyle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("style_id").forGetter(EquippedStyle::getStyleId),
            Codec.INT.optionalFieldOf("variant", -1).forGetter(EquippedStyle::getVariant)
    ).apply(instance, EquippedStyle::decode));
}
