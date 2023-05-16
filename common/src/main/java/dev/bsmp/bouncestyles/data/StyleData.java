package dev.bsmp.bouncestyles.data;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StyleData {
    private @Nullable Style headStyle;
    private @Nullable Style bodyStyle;
    private @Nullable Style legStyle;
    private @Nullable Style feetStyle;
    private boolean showArmor = true;
    private List<Identifier> unlocks;
    private final List<String> hiddenParts = new ArrayList<>();

    public StyleData(@Nullable Style headStyle, @Nullable Style bodyStyle, @Nullable Style legStyle, @Nullable Style feetStyle) {
        this(headStyle, bodyStyle, legStyle, feetStyle, new ArrayList<>());
    }

    public StyleData(@Nullable Style headStyle, @Nullable Style bodyStyle, @Nullable Style legStyle, @Nullable Style feetStyle, List<Identifier> unlocks) {
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

    public List<Identifier> getUnlocks() {
        return this.unlocks;
    }

    public void setUnlocks(List<Identifier> unlocks) {
        this.unlocks = unlocks;
    }

    public boolean unlockStyle(Style style) {
        if(style == null)
            return false;
        return unlockStyle(style.styleId);
    }
    public boolean unlockStyle(Identifier styleId) {
        if(styleId == null || unlocks.contains(styleId))
            return false;
        return unlocks.add(styleId);
    }

    public boolean removeStyle(Style style) {
        if(style == null)
            return false;
        return removeStyle(style.styleId);
    }
    public boolean removeStyle(Identifier styleId) {
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
    public boolean hasStyleUnlocked(Identifier id) {
        return unlocks.contains(id);
    }

    public StylePreset createPreset(String presetName) {
        Identifier head = this.headStyle != null ? this.headStyle.styleId : null;
        Identifier body = this.bodyStyle != null ? this.bodyStyle.styleId : null;
        Identifier legs = this.legStyle != null ? this.legStyle.styleId : null;
        Identifier feet = this.feetStyle != null ? this.feetStyle.styleId : null;
        boolean error = StylePreset.checkIds(head, body, legs, feet);
        return new StylePreset(new Identifier(BounceStyles.modId, presetName.toLowerCase().replace(" ", "_")), presetName, head, body, legs, feet, error);
    }

    //Static
    public static void setPlayerData(PlayerEntity player, StyleData styleData) {
        ((StyleEntity)player).setStyleData(styleData);
    }

    public static StyleData getOrCreateStyleData(PlayerEntity player) {
        return ((StyleEntity)player).getOrCreateStyleData();
    }

    private static void convertStyle(NbtCompound tag, Style style, String slot) {
        if(style != null)
            tag.putString(slot, style.styleId.toString());
    }

    private static @Nullable Style parseStyle(NbtCompound tag, String slot) {
        Style style = null;
        if(tag.contains(slot))
            style = StyleRegistry.REGISTRY.get(Identifier.tryParse(tag.getString(slot)));
        return style;
    }

    public static NbtCompound toNBT(StyleData styleData) {
        NbtCompound tag = equippedToNBT(styleData);
        tag.put("unlocks", unlocksToNBT(styleData));
        return tag;
    }

    public static NbtCompound equippedToNBT(StyleData styleData) {
        NbtCompound tag = new NbtCompound();
        convertStyle(tag, styleData.headStyle, StyleRegistry.Category.Head.name());
        convertStyle(tag, styleData.bodyStyle, StyleRegistry.Category.Body.name());
        convertStyle(tag, styleData.legStyle, StyleRegistry.Category.Legs.name());
        convertStyle(tag, styleData.feetStyle, StyleRegistry.Category.Feet.name());
        tag.putBoolean("armorVisible", styleData.isArmorVisible());
        return tag;
    }

    public static NbtList unlocksToNBT(StyleData styleData) {
        NbtList list = new NbtList();
        for(Identifier id : styleData.unlocks) {
            if(id == null)
                continue;
            list.add(NbtString.of(id.toString()));
        }
        return list;
    }

    public static StyleData fromNBT(NbtCompound tag) {
        StyleData styleData = equippedFromNBT(tag);
        styleData.unlocks = unlocksFromNBT(tag.getList("unlocks", NbtElement.STRING_TYPE));
        return styleData;
    }

    public static StyleData equippedFromNBT(NbtCompound tag) {
        StyleData styleData = new StyleData(
                parseStyle(tag, StyleRegistry.Category.Head.name()),
                parseStyle(tag, StyleRegistry.Category.Body.name()),
                parseStyle(tag, StyleRegistry.Category.Legs.name()),
                parseStyle(tag, StyleRegistry.Category.Feet.name())
        );
        styleData.showArmor = tag.getBoolean("armorVisible");
        return styleData;
    }

    public static List<Identifier> unlocksFromNBT(NbtList unlocksTag) {
        List<Identifier> list = new ArrayList<>();
        for(NbtElement t : unlocksTag) {
            list.add(Identifier.tryParse(t.asString()));
        }
        return list;
    }

    public static void copyFrom(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean b) {
        //Pre-Architectury 4.11.91 Fix
//        ServerPlayerEntity oldPlayer = Platform.isFabric() ? player2 : player1;
//        ServerPlayerEntity newPlayer = Platform.isFabric() ? player1 : player2;

        StyleData styleData = StyleData.getOrCreateStyleData(oldPlayer);
        StyleData.setPlayerData(newPlayer, styleData);
    }
}

