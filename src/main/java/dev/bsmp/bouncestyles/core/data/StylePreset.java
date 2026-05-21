package dev.bsmp.bouncestyles.core.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;

import java.util.Optional;

public record StylePreset(Optional<EquippedStyle> head, Optional<EquippedStyle> body, Optional<EquippedStyle> legs, Optional<EquippedStyle> feet) {
    public StylePreset(EquippedStyle head, EquippedStyle body, EquippedStyle legs, EquippedStyle feet) {
        this(slotCheck(head), slotCheck(body), slotCheck(legs), slotCheck(feet));
    }

    private static Optional<EquippedStyle> slotCheck(EquippedStyle equipped) {
        if (equipped.getStyleId().isEmpty()) return Optional.empty();
        return Optional.of(equipped);
    }

    public static Error errorCheck(StyleData styleData, EquippedStyle... slots) {
        Error error = Error.NO_ERROR;

        for(EquippedStyle slot : slots)
            if(slot.getStyleId().isPresent()) {
                var styleId = slot.getStyleId().get();
                if (!BounceStylesRegistries.idExists(styleId))
                    error = error != Error.NO_ERROR ? Error.BOTH : Error.MISSING;
                else if (!styleData.hasStyleUnlocked(styleId))
                    error = error != Error.NO_ERROR ? Error.BOTH : Error.LOCKED;
            }

        return error;
    }

    public static final Codec<StylePreset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EquippedStyle.CODEC.optionalFieldOf("head").forGetter(StylePreset::head),
            EquippedStyle.CODEC.optionalFieldOf("body").forGetter(StylePreset::body),
            EquippedStyle.CODEC.optionalFieldOf("legs").forGetter(StylePreset::legs),
            EquippedStyle.CODEC.optionalFieldOf("feet").forGetter(StylePreset::feet)
    ).apply(instance, StylePreset::new));

    enum Error {
        NO_ERROR,
        MISSING,
        LOCKED,
        BOTH
    }

}