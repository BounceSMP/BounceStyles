package dev.bsmp.bouncestyles.data;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlayerStyleData {
    private @Nullable Style headStyle;
    private @Nullable Style bodyStyle;
    private @Nullable Style legStyle;
    private @Nullable Style feetStyle;
    private List<ResourceLocation> unlocks;
    private List<String> hiddenParts = new ArrayList<>();

    public PlayerStyleData(@Nullable Style headStyle, @Nullable Style bodyStyle, @Nullable Style legStyle, @Nullable Style feetStyle) {
        this(headStyle, bodyStyle, legStyle, feetStyle, new ArrayList<>());
    }

    public PlayerStyleData(@Nullable Style headStyle, @Nullable Style bodyStyle, @Nullable Style legStyle, @Nullable Style feetStyle, List<ResourceLocation> unlocks) {
        setHeadStyle(headStyle);
        setBodyStyle(bodyStyle);
        setLegStyle(legStyle);
        setFeetStyle(feetStyle);
        setUnlocks(unlocks);
    }

    public void setHeadStyle(Style headStyle) {
        this.headStyle = headStyle;
        updateVisibility(headStyle);
    }
    public void setBodyStyle(Style bodyStyle) {
        this.bodyStyle = bodyStyle;
        updateVisibility(bodyStyle);
    }
    public void setLegStyle(Style legStyle) {
        this.legStyle = legStyle;
        updateVisibility(legStyle);
    }
    public void setFeetStyle(Style feetStyle) {
        this.feetStyle = feetStyle;
        updateVisibility(feetStyle);
    }

    private void updateVisibility(Style style) {
        if(style == null || style.hiddenParts == null)
            return;
        for(String s : style.hiddenParts) {
            if(!this.hiddenParts.contains(s))
                this.hiddenParts.add(s);
        }
    }
    public List<String> getHiddenParts() {
        return this.hiddenParts;
    }

    public Style getHeadStyle() {
        return headStyle;
    }
    public Style getBodyStyle() {
        return bodyStyle;
    }
    public Style getLegStyle() {
        return legStyle;
    }
    public Style getFeetStyle() {
        return feetStyle;
    }

    public Style getStyleForSlot(StyleLoader.Category category) {
        return switch (category) {
            case Head -> this.headStyle;
            case Body -> this.bodyStyle;
            case Legs -> this.legStyle;
            case Feet -> this.feetStyle;
            case Preset -> null;
        };
    }

    public List<ResourceLocation> getUnlocks() {
        return this.unlocks;
    }
    public void setUnlocks(List<ResourceLocation> unlocks) {
        this.unlocks = unlocks;
    }
    public boolean unlockStyle(Style style) {
        return unlockStyle(style.styleId);
    }
    public boolean unlockStyle(ResourceLocation styleId) {
        if(unlocks.contains(styleId))
            return false;
        return unlocks.add(styleId);
    }
    public boolean hasStyleUnlocked(Style style) {
        return hasStyleUnlocked(style.styleId);
    }
    public boolean hasStyleUnlocked(ResourceLocation id) {
        return unlocks.contains(id);
    }
    public StylePreset createPreset(String presetName) {
        ResourceLocation head = this.headStyle != null ? this.headStyle.styleId : null;
        ResourceLocation body = this.bodyStyle != null ? this.bodyStyle.styleId : null;
        ResourceLocation legs = this.legStyle != null ? this.legStyle.styleId : null;
        ResourceLocation feet = this.feetStyle != null ? this.feetStyle.styleId : null;
        boolean error = StylePreset.checkIds(head, body, legs, feet);
        return new StylePreset(new ResourceLocation(BounceStyles.modId, presetName.toLowerCase().replace(" ", "_")), presetName, head, body, legs, feet, error);
    }

    //Static
    public static void setPlayerData(Player player, PlayerStyleData styleData) {
        ((StyleEntity)player).setStyleData(styleData);
    }

    public static PlayerStyleData getPlayerData(Player player) {
        return ((StyleEntity)player).getStyleData();
    }

    private static void convertStyle(CompoundTag tag, Style style, String slot) {
        if(style != null)
            tag.putString(slot, style.styleId.toString());
    }

    private static @Nullable Style parseStyle(CompoundTag tag, String slot) {
        Style style = null;
        if(tag.contains(slot))
            style = StyleLoader.REGISTRY.get(ResourceLocation.tryParse(tag.getString(slot)));
        return style;
    }

    public static CompoundTag toNBT(PlayerStyleData styleData) {
        CompoundTag tag = equippedToNBT(styleData);
        tag.put("unlocks", unlocksToNBT(styleData));
        return tag;
    }

    public static CompoundTag equippedToNBT(PlayerStyleData styleData) {
        CompoundTag tag = new CompoundTag();
        convertStyle(tag, styleData.headStyle, StyleLoader.Category.Head.name());
        convertStyle(tag, styleData.bodyStyle, StyleLoader.Category.Body.name());
        convertStyle(tag, styleData.legStyle, StyleLoader.Category.Legs.name());
        convertStyle(tag, styleData.feetStyle, StyleLoader.Category.Feet.name());
        return tag;
    }

    public static ListTag unlocksToNBT(PlayerStyleData styleData) {
        ListTag list = new ListTag();
        for(ResourceLocation id : styleData.unlocks) {
            list.add(StringTag.valueOf(id.toString()));
        }
        return list;
    }

    public static PlayerStyleData fromNBT(CompoundTag tag) {
        PlayerStyleData styleData = equippedFromNBT(tag);
        styleData.unlocks = unlocksFromNBT(tag.getList("unlocks", Tag.TAG_STRING));
        return styleData;
    }

    public static PlayerStyleData equippedFromNBT(CompoundTag tag) {
        return new PlayerStyleData(
                parseStyle(tag, StyleLoader.Category.Head.name()),
                parseStyle(tag, StyleLoader.Category.Body.name()),
                parseStyle(tag, StyleLoader.Category.Legs.name()),
                parseStyle(tag, StyleLoader.Category.Feet.name())
        );
    }

    public static List<ResourceLocation> unlocksFromNBT(ListTag unlocksTag) {
        List<ResourceLocation> list = new ArrayList<>();
        for(Tag t : unlocksTag) {
            list.add(ResourceLocation.tryParse(t.getAsString()));
        }
        return list;
    }
}
