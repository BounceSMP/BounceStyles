package dev.bsmp.bouncestyles.api.style;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum RenderType implements StringRepresentable {
    Cull, //Default
    No_Cull,
    Translucent;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }

    public static final Codec<RenderType> CODEC = StringRepresentable.fromEnum(RenderType::values);
}
