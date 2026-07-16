package dev.bsmp.bouncestyles.api.style;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.bsmp.bouncestyles.api.data.EquippedStyle;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.data.unlocks.UnlockManager;
import net.minecraft.world.entity.Entity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public record StylePreset(Optional<EquippedStyle> head, Optional<EquippedStyle> body, Optional<EquippedStyle> legs, Optional<EquippedStyle> feet) {
    public StylePreset(EquippedStyle head, EquippedStyle body, EquippedStyle legs, EquippedStyle feet) {
        this(slotCheck(head), slotCheck(body), slotCheck(legs), slotCheck(feet));
    }

    public Map<Category, EquippedStyle> getAllNonEmpty() {
        var map = new LinkedHashMap<Category, EquippedStyle>();
        head.ifPresent(equipped -> map.put(Category.Head, equipped));
        body.ifPresent(equipped -> map.put(Category.Body, equipped));
        legs.ifPresent(equipped -> map.put(Category.Legs, equipped));
        feet.ifPresent(equipped -> map.put(Category.Feet, equipped));
        return map;
    }

    public Map<Category, EquippedStyle> toMap() {
        return Map.of(
                Category.Head, head().orElse(new EquippedStyle()),
                Category.Body, body().orElse(new EquippedStyle()),
                Category.Legs, legs().orElse(new EquippedStyle()),
                Category.Feet, feet().orElse(new EquippedStyle())
        );
    }

    private static Optional<EquippedStyle> slotCheck(EquippedStyle equipped) {
        if (equipped.getStyle().isEmpty()) return Optional.empty();
        return Optional.of(equipped);
    }

    public static Error errorCheck(Entity entity, EquippedStyle slot) {
        Error error = Error.NO_ERROR;

        if(slot.getStyleId().isPresent()) {
            var styleId = slot.getStyleId().get();
            if (!BounceStylesRegistries.idExists(styleId))
                error = Error.MISSING;
            else if (!UnlockManager.hasUnlocked(entity, styleId))
                error = Error.LOCKED;
        }

        return error;
    }

    public static final Codec<StylePreset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EquippedStyle.CODEC.optionalFieldOf("head").forGetter(StylePreset::head),
            EquippedStyle.CODEC.optionalFieldOf("body").forGetter(StylePreset::body),
            EquippedStyle.CODEC.optionalFieldOf("legs").forGetter(StylePreset::legs),
            EquippedStyle.CODEC.optionalFieldOf("feet").forGetter(StylePreset::feet)
    ).apply(instance, StylePreset::new));

    public enum Error {
        NO_ERROR(""),
        MISSING("Style with this ID does not exist"),
        LOCKED("You do not have this Style unlocked");

        public final String message;

        Error(String message) {
            this.message = message;
        }
    }

}