package dev.bsmp.bouncestyles.api.style;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public enum Category implements StringRepresentable {
    Head(Style.HEAD_ICON), Body(Style.BODY_ICON), Legs(Style.LEGS_ICON), Feet(Style.FEET_ICON), Preset(Style.PRESET_ICON);

    public final ResourceLocation categoryIcon;

    Category(ResourceLocation categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }

    public static final Codec<Category> CODEC = StringRepresentable.fromEnum(Category::values);
}
