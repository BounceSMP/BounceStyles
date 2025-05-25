package dev.bsmp.bouncestyles.api.style;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.data.StyleData;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record StylePreset(ResourceLocation presetId, String name, Optional<Pair<ResourceLocation, Integer>> head, Optional<Pair<ResourceLocation, Integer>> body, Optional<Pair<ResourceLocation, Integer>> legs, Optional<Pair<ResourceLocation, Integer>> feet, boolean error) {
    public StylePreset(ResourceLocation presetId, String name, Optional<Pair<ResourceLocation, Integer>> head, Optional<Pair<ResourceLocation, Integer>> body, Optional<Pair<ResourceLocation, Integer>> legs, Optional<Pair<ResourceLocation, Integer>> feet) {
        this(presetId, name, head, body, legs, feet, errorCheck(head, body, legs, feet));
    }

    public StylePreset(String name, Optional<Pair<ResourceLocation, Integer>> head, Optional<Pair<ResourceLocation, Integer>> body, Optional<Pair<ResourceLocation, Integer>> legs, Optional<Pair<ResourceLocation, Integer>> feet) {
        this(BounceStyles.resourceLocation(name), name, head, body, legs, feet);
    }

    public static Optional<StylePreset> fromJson(ResourceLocation presetId, JsonObject json) {
        return CODEC.parse(JsonOps.INSTANCE, json).resultOrPartial(BounceStyles.LOGGER::error);
    }

    public static boolean errorCheck(Optional<Pair<ResourceLocation, Integer>>... slots) {
        for(Optional<Pair<ResourceLocation, Integer>> slot : slots)
            if(slot.isPresent() && !BounceStylesRegistries.idExists(slot.get().getFirst()))
                return true;

        return false;
    }

    public boolean hasAllUnlocked(StyleData styleData) {
        boolean result = true;
        if (head.isPresent()) result &= styleData.hasStyleUnlocked(head.get().getFirst());
        if (body.isPresent()) result &= styleData.hasStyleUnlocked(body.get().getFirst());
        if (legs.isPresent()) result &= styleData.hasStyleUnlocked(legs.get().getFirst());
        if (feet.isPresent()) result &= styleData.hasStyleUnlocked(feet.get().getFirst());
        return result;
    }

    public static final Codec<StylePreset> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(StylePreset::name),
            StyleData.CODEC_PAIR.optionalFieldOf("head").forGetter(StylePreset::head),
            StyleData.CODEC_PAIR.optionalFieldOf("body").forGetter(StylePreset::body),
            StyleData.CODEC_PAIR.optionalFieldOf("legs").forGetter(StylePreset::legs),
            StyleData.CODEC_PAIR.optionalFieldOf("feet").forGetter(StylePreset::feet)
    ).apply(instance, StylePreset::new));

}