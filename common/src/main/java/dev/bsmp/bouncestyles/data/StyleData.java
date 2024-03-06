package dev.bsmp.bouncestyles.data;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class StyleData {
    private @Nullable Style headStyle;
    private @Nullable Style bodyStyle;
    private @Nullable Style legStyle;
    private @Nullable Style feetStyle;
    private boolean showArmor = true;
    private List<ResourceLocation> unlocks;
    private final List<String> hiddenParts = new ArrayList<>();

    public StyleData(@Nullable Style headStyle, @Nullable Style bodyStyle, @Nullable Style legStyle, @Nullable Style feetStyle) {
        this(headStyle, bodyStyle, legStyle, feetStyle, new ArrayList<>());
    }

    public StyleData(@Nullable Style headStyle, @Nullable Style bodyStyle, @Nullable Style legStyle, @Nullable Style feetStyle, List<ResourceLocation> unlocks) {
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

    public void setArmorVisibility(boolean showArmor) {
        this.showArmor = showArmor;
    }
    public void toggleArmorVisibility() {
        this.showArmor = !this.showArmor;
    }

    private void updateVisibility(Style style) {
        if(style == null || style.hiddenParts == null)
            return;
        for(String s : style.hiddenParts) {
            if(!this.hiddenParts.contains(s))
                this.hiddenParts.add(s);
        }
    }

    public @Nullable Style getHeadStyle() {
        return headStyle;
    }
    public @Nullable Style getBodyStyle() {
        return bodyStyle;
    }
    public @Nullable Style getLegStyle() {
        return legStyle;
    }
    public @Nullable Style getFeetStyle() {
        return feetStyle;
    }

    public boolean isArmorVisible() {
        return this.showArmor;
    }

    public List<String> getHiddenParts() {
        return this.hiddenParts;
    }

    public Style getStyleForSlot(StyleRegistry.Category category) {
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
        if(style == null)
            return false;
        return unlockStyle(style.styleId);
    }
    public boolean unlockStyle(ResourceLocation styleId) {
        if(styleId == null || unlocks.contains(styleId))
            return false;
        return unlocks.add(styleId);
    }

    public boolean removeStyle(Style style) {
        if(style == null)
            return false;
        return removeStyle(style.styleId);
    }
    public boolean removeStyle(ResourceLocation styleId) {
        boolean b = unlocks.remove(styleId);
        if(b) {
            if(headStyle != null && headStyle.styleId == styleId)
                setHeadStyle(null);
            if(bodyStyle != null && bodyStyle.styleId == styleId)
                setBodyStyle(null);
            if(legStyle != null && legStyle.styleId == styleId)
                setLegStyle(null);
            if(feetStyle != null && feetStyle.styleId == styleId)
                setFeetStyle(null);
        }
        return b;
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
    public static void setPlayerData(Player player, StyleData styleData) {
        ((StyleEntity)player).setStyleData(styleData);
    }

    public static StyleData getOrCreateStyleData(Player player) {
        return ((StyleEntity)player).getOrCreateStyleData();
    }

    private static void convertStyle(CompoundTag tag, Style style, String slot) {
        if(style != null)
            tag.putString(slot, style.styleId.toString());
    }

    private static @Nullable Style parseStyle(CompoundTag tag, String slot) {
        Style style = null;
        if(tag.contains(slot))
            style = StyleRegistry.getStyle(ResourceLocation.tryParse(tag.getString(slot)));
        return style;
    }

    public static CompoundTag toNBT(StyleData styleData) {
        CompoundTag tag = equippedToNBT(styleData);
        tag.put("unlocks", unlocksToNBT(styleData));
        return tag;
    }

    public static CompoundTag equippedToNBT(StyleData styleData) {
        CompoundTag tag = new CompoundTag();
        convertStyle(tag, styleData.headStyle, StyleRegistry.Category.Head.name());
        convertStyle(tag, styleData.bodyStyle, StyleRegistry.Category.Body.name());
        convertStyle(tag, styleData.legStyle, StyleRegistry.Category.Legs.name());
        convertStyle(tag, styleData.feetStyle, StyleRegistry.Category.Feet.name());
        tag.putBoolean("armorVisible", styleData.isArmorVisible());
        return tag;
    }

    public static ListTag unlocksToNBT(StyleData styleData) {
        ListTag list = new ListTag();
        for(ResourceLocation id : styleData.unlocks) {
            if(id == null)
                continue;
            list.add(StringTag.valueOf(id.toString()));
        }
        return list;
    }

    public static StyleData fromNBT(CompoundTag tag) {
        StyleData styleData = equippedFromNBT(tag);
        styleData.unlocks = unlocksFromNBT(tag.getList("unlocks", Tag.TAG_STRING));
        return styleData;
    }

    public static StyleData equippedFromNBT(CompoundTag tag) {
        StyleData styleData = new StyleData(
                parseStyle(tag, StyleRegistry.Category.Head.name()),
                parseStyle(tag, StyleRegistry.Category.Body.name()),
                parseStyle(tag, StyleRegistry.Category.Legs.name()),
                parseStyle(tag, StyleRegistry.Category.Feet.name())
        );
        styleData.showArmor = tag.getBoolean("armorVisible");
        return styleData;
    }

    public static List<ResourceLocation> unlocksFromNBT(ListTag unlocksTag) {
        List<ResourceLocation> list = new ArrayList<>();
        for(Tag t : unlocksTag) {
            list.add(ResourceLocation.tryParse(t.getAsString()));
        }
        return list;
    }

    public static void copyFrom(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean b) {
        //Pre-Architectury 4.11.91 Fix
//        ServerPlayerEntity oldPlayer = Platform.isFabric() ? player2 : player1;
//        ServerPlayerEntity newPlayer = Platform.isFabric() ? player1 : player2;

        StyleData styleData = StyleData.getOrCreateStyleData(oldPlayer);
        StyleData.setPlayerData(newPlayer, styleData);
    }
}

