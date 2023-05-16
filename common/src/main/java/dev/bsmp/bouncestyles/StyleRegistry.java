package dev.bsmp.bouncestyles;

import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.data.StyleMagazineItem;
import dev.bsmp.bouncestyles.data.StylePreset;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class StyleRegistry {
    public static final HashMap<Identifier, Style> REGISTRY = new HashMap<>();
    public static final HashMap<Identifier, StylePreset> PRESETS = new HashMap<>();

    public static Style getStyle(Identifier id) {
        return REGISTRY.get(id);
    }

    @Nullable
    public static Style getStyleFromStack(ItemStack itemStack) {
        if(!(itemStack.getItem() instanceof StyleMagazineItem))
            return null;
        return getStyle(getStyleIdFromStack(itemStack));
    }

    @Nullable public static Identifier getStyleIdFromStack(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        if(nbt == null || !nbt.contains("styleId"))
            return null;
        return Identifier.tryParse(nbt.getString("styleId"));
    }

    public static StylePreset createPreset(StyleData styleData, String presetName) {
        StylePreset newPreset = styleData.createPreset(presetName);
        PRESETS.put(newPreset.presetId(), newPreset);
        StyleLoader.writePresetsFile();
        return newPreset;
    }

    public static boolean idExists(Identifier id) {
        return REGISTRY.containsKey(id);
    }

    public static final Identifier HEAD_ICON = new Identifier(BounceStyles.modId, "textures/icon/bounce_head.png");
    public static final Identifier BODY_ICON = new Identifier(BounceStyles.modId, "textures/icon/bounce_body.png");
    public static final Identifier LEGS_ICON = new Identifier(BounceStyles.modId, "textures/icon/bounce_legs.png");
    public static final Identifier FEET_ICON = new Identifier(BounceStyles.modId, "textures/icon/bounce_feet.png");
    public static final Identifier PRESET_ICON = new Identifier(BounceStyles.modId, "textures/icon/bounce_preset.png");

    public enum Category {
        Head(HEAD_ICON), Body(BODY_ICON), Legs(LEGS_ICON), Feet(FEET_ICON), Preset(PRESET_ICON);

        public final Identifier categoryIcon;

        Category(Identifier categoryIcon) {
            this.categoryIcon = categoryIcon;
        }
    }
}
