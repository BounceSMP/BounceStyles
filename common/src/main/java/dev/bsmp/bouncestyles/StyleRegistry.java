package dev.bsmp.bouncestyles;

import dev.bsmp.bouncestyles.data.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class StyleRegistry {
    private static final HashMap<Identifier, Style> REGISTRY = new HashMap<>();
    public static final HashMap<Identifier, StylePreset> PRESETS = new HashMap<>();

    public static void registerStyle(Identifier id, Style style) {
        if(id == null || style == null) {
            BounceStyles.LOGGER.warn("Tried to register a Style with a null value. [id="+id+", style=" + style + "]");
            return;
        }
        REGISTRY.put(id, style);
    }

    public static Style getStyle(Identifier id) {
        Style style = REGISTRY.get(id);
        if(style == null) style = MissingStyle.INSTANCE;
        return style;
    }

    @Nullable
    public static Style getStyleFromStack(ItemStack itemStack) {
        if(!(itemStack.getItem() instanceof StyleMagazineItem))
            return null;
        return getStyle(getStyleIdFromStack(itemStack));
    }

    static void clearRegistry() {
        REGISTRY.clear();
    }

    @Nullable public static Identifier getStyleIdFromStack(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        if(nbt == null || !nbt.contains("styleId"))
            return null;
        return Identifier.tryParse(nbt.getString("styleId"));
    }

    public static Set<Identifier> getAllStyleIds() {
        return REGISTRY.keySet();
    }

    public static Collection<Style> getAllStyles() {
        return REGISTRY.values();
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
