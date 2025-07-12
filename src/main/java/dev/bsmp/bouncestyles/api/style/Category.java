package dev.bsmp.bouncestyles.api.style;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public enum Category implements StringRepresentable {
    Head, Body, Legs, Feet, Preset;

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }

    public static final Codec<Category> CODEC = StringRepresentable.fromEnum(Category::values);
}
