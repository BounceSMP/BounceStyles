package dev.bsmp.bouncestyles.core.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.api.style.StylePreset;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries.Category;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StyleData {
    private @Nullable Pair<Style, Integer> headStyle;
    private @Nullable Pair<Style, Integer> bodyStyle;
    private @Nullable Pair<Style, Integer> legStyle;
    private @Nullable Pair<Style, Integer> feetStyle;

    private boolean showArmor = true;
    private List<String> hiddenParts = new ArrayList<>();
    private List<ResourceLocation> unlocks = new ArrayList<>();

    public StyleData(Optional<Pair<Style, Integer>> head, Optional<Pair<Style, Integer>> body, Optional<Pair<Style, Integer>> legs, Optional<Pair<Style, Integer>> feet) {
        this(head, body, legs, feet, Optional.empty(), Optional.empty(), Optional.empty());
    }

    public StyleData(Optional<Pair<Style, Integer>> head, Optional<Pair<Style, Integer>> body, Optional<Pair<Style, Integer>> legs, Optional<Pair<Style, Integer>> feet,  Optional<Boolean> armorVisible, Optional<List<String>> hiddenParts, Optional<List<ResourceLocation>> unlocks) {
        head.ifPresent(pair -> this.setHeadStyle(pair.getFirst(), pair.getSecond()));
        body.ifPresent(pair -> this.setBodyStyle(pair.getFirst(), pair.getSecond()));
        legs.ifPresent(pair -> this.setLegStyle(pair.getFirst(), pair.getSecond()));
        feet.ifPresent(pair -> this.setFeetStyle(pair.getFirst(), pair.getSecond()));
        this.setArmorVisibility(armorVisible.orElse(true));
        hiddenParts.ifPresent(list -> this.hiddenParts = new ArrayList<>(list));
        unlocks.ifPresent(list -> this.unlocks = new ArrayList<>(list));
    }

    public void setHeadStyle(Style headStyle) {
        setHeadStyle(headStyle, -1);
    }

    public void setHeadStyle(Style headStyle, int textureVariant) {
        this.headStyle = Pair.of(headStyle, textureVariant);
        updateVisibility(headStyle);
    }

    public void setBodyStyle(Style bodyStyle) {
        setBodyStyle(bodyStyle, -1);
    }

    public void setBodyStyle(Style bodyStyle, int textureVariant) {
        this.bodyStyle = Pair.of(bodyStyle, textureVariant);
        updateVisibility(bodyStyle);
    }

    public void setLegStyle(Style legStyle) {
        setLegStyle(legStyle, -1);
    }

    public void setLegStyle(Style legStyle, int textureVariant) {
        this.legStyle = Pair.of(legStyle, textureVariant);
        updateVisibility(legStyle);
    }

    public void setFeetStyle(Style feetStyle) {
        setFeetStyle(feetStyle, -1);
    }

    public void setFeetStyle(Style feetStyle, int textureVariant) {
        this.feetStyle = Pair.of(feetStyle, textureVariant);
        updateVisibility(feetStyle);
    }

    public void setArmorVisibility(boolean showArmor) {
        this.showArmor = showArmor;
    }

    public void toggleArmorVisibility() {
        this.showArmor = !this.showArmor;
    }

    private void updateVisibility(Style style) {
        if(style == null || style.getHiddenParts().isEmpty())
            return;
        for(String s : style.getHiddenParts().get()) {
            if(!this.hiddenParts.contains(s))
                this.hiddenParts.add(s);
        }
    }

    public Optional<Pair<Style, Integer>> getHeadStyle() {
        return Optional.ofNullable(this.headStyle);
    }

    public Optional<Pair<Style, Integer>> getBodyStyle() {
        return Optional.ofNullable(this.bodyStyle);
    }

    public Optional<Pair<Style, Integer>> getLegStyle() {
        return Optional.ofNullable(this.legStyle);
    }

    public Optional<Pair<Style, Integer>> getFeetStyle() {
        return Optional.ofNullable(this.feetStyle);
    }

    public boolean isArmorVisible() {
        return this.showArmor;
    }

    public List<String> getHiddenParts() {
        return this.hiddenParts;
    }

    public Optional<Pair<Style, Integer>> getStyleForSlot(Category category) {
        return switch (category) {
            case Head -> getHeadStyle();
            case Body -> getBodyStyle();
            case Legs -> getLegStyle();
            case Feet -> getFeetStyle();
            case Preset -> Optional.empty();
        };
    }

    public void setStyleForSlot(Category category, Style style, int textureId) {
        switch (category) {
            case Head -> setHeadStyle(style, textureId);
            case Body -> setBodyStyle(style, textureId);
            case Legs -> setLegStyle(style, textureId);
            case Feet -> setFeetStyle(style, textureId);
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
        return unlockStyle(style.getStyleId());
    }

    public boolean unlockStyle(ResourceLocation styleId) {
        if(styleId == null || unlocks.contains(styleId))
            return false;
        return unlocks.add(styleId);
    }

    public boolean removeStyle(Style style) {
        if(style == null)
            return false;
        return removeStyle(style.getStyleId());
    }

    public boolean removeStyle(ResourceLocation styleId) {
        boolean b = unlocks.remove(styleId);
        if(b) {
            if(headStyle != null && headStyle.getFirst().getStyleId() == styleId)
                setHeadStyle(null);
            if(bodyStyle != null && bodyStyle.getFirst().getStyleId() == styleId)
                setBodyStyle(null);
            if(legStyle != null && legStyle.getFirst().getStyleId() == styleId)
                setLegStyle(null);
            if(feetStyle != null && feetStyle.getFirst().getStyleId() == styleId)
                setFeetStyle(null);
        }
        return b;
    }

    //ToDo Potentially move unlocks to a file and read as needed
    public boolean hasStyleUnlocked(Style style) {
        return hasStyleUnlocked(style.getStyleId());
    }

    public boolean hasStyleUnlocked(ResourceLocation id) {
        return unlocks.contains(id);
    }

    public StylePreset createPreset(String presetName) {
        Optional<Pair<ResourceLocation, Integer>> head = this.headStyle != null ? Optional.of(Pair.of(this.headStyle.getFirst().getStyleId(), this.headStyle.getSecond())) : Optional.empty();
        Optional<Pair<ResourceLocation, Integer>> body = this.bodyStyle != null ? Optional.of(Pair.of(this.bodyStyle.getFirst().getStyleId(), this.bodyStyle.getSecond())) : Optional.empty();
        Optional<Pair<ResourceLocation, Integer>> legs = this.legStyle != null ? Optional.of(Pair.of(this.legStyle.getFirst().getStyleId(), this.legStyle.getSecond())) : Optional.empty();
        Optional<Pair<ResourceLocation, Integer>> feet = this.feetStyle != null ? Optional.of(Pair.of(this.feetStyle.getFirst().getStyleId(), this.feetStyle.getSecond())) : Optional.empty();
        return new StylePreset(BounceStyles.resourceLocation(presetName.toLowerCase().replace(" ", "_")), presetName, head, body, legs, feet);
    }

    //Static
    public static void setPlayerData(Player player, StyleData styleData) {
        ((StyleEntity)player).setStyleData(styleData);
    }

    public static StyleData getOrCreateStyleData(Player player) {
        return ((StyleEntity)player).getOrCreateStyleData();
    }

    public static Optional<Tag> toNBT(StyleData styleData) {
        return CODEC_FULL.encodeStart(NbtOps.INSTANCE, styleData).resultOrPartial(BounceStyles.LOGGER::error);
    }

    public static Optional<Tag> equippedToNBT(StyleData styleData) {
        return CODEC_EQUIPPED.encodeStart(NbtOps.INSTANCE, styleData).resultOrPartial(BounceStyles.LOGGER::error);
    }

    public static Optional<StyleData> fromNBT(CompoundTag tag) {
        return CODEC_FULL.parse(NbtOps.INSTANCE, tag).resultOrPartial(BounceStyles.LOGGER::error);
    }

    public static Optional<StyleData> equippedFromNBT(CompoundTag tag) {
        return CODEC_EQUIPPED.parse(NbtOps.INSTANCE, tag).resultOrPartial(BounceStyles.LOGGER::error);
    }

    public static void copyFrom(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        StyleData styleData = StyleData.getOrCreateStyleData(oldPlayer);
        StyleData.setPlayerData(newPlayer, styleData);
    }

    private static StyleData decode(Optional<Pair<ResourceLocation, Integer>> head, Optional<Pair<ResourceLocation, Integer>> body, Optional<Pair<ResourceLocation, Integer>> legs, Optional<Pair<ResourceLocation, Integer>> feet, Optional<Boolean> armorVisible) {
        return decode(head, body, legs, feet, armorVisible, Optional.empty(), Optional.empty());
    }

    private static StyleData decode(Optional<Pair<ResourceLocation, Integer>> head, Optional<Pair<ResourceLocation, Integer>> body, Optional<Pair<ResourceLocation, Integer>> legs, Optional<Pair<ResourceLocation, Integer>> feet, Optional<Boolean> armorVisible, Optional<List<String>> hiddenParts, Optional<List<ResourceLocation>> unlocks) {
        return new StyleData(idToStyle(head), idToStyle(body), idToStyle(legs), idToStyle(feet), armorVisible, hiddenParts, unlocks);
    }

    private static Optional<Pair<Style, Integer>> idToStyle(Optional<Pair<ResourceLocation, Integer>> slot) {
        if (slot.isPresent()) {
            Optional<Style> style = BounceStylesRegistries.getStyle(slot.get().getFirst());
            if (style.isPresent())
                return Optional.of(Pair.of(style.get(), slot.get().getSecond()));
        }

        return Optional.empty();
    }
    
    public static final Codec<Pair<ResourceLocation, Integer>> CODEC_PAIR = Codec.pair(ResourceLocation.CODEC.fieldOf("style_id").codec(), Codec.INT.fieldOf("texture_variant").codec());

    public static final Codec<StyleData> CODEC_FULL = RecordCodecBuilder.create(instance -> instance.group(
            CODEC_PAIR.optionalFieldOf("head").forGetter(styleData -> getIdIntPair(styleData, Category.Head)),
            CODEC_PAIR.optionalFieldOf("body").forGetter(styleData -> getIdIntPair(styleData, Category.Body)),
            CODEC_PAIR.optionalFieldOf("legs").forGetter(styleData -> getIdIntPair(styleData, Category.Legs)),
            CODEC_PAIR.optionalFieldOf("feet").forGetter(styleData -> getIdIntPair(styleData, Category.Feet)),
            Codec.BOOL.optionalFieldOf("show_armor").forGetter(styleData -> Optional.of(styleData.isArmorVisible())),
            Codec.STRING.listOf().optionalFieldOf("hidden_parts").forGetter(styleData -> Optional.of(styleData.getHiddenParts())),
            ResourceLocation.CODEC.listOf().optionalFieldOf("unlocks").forGetter(styleData -> Optional.of(styleData.unlocks))
    ).apply(instance, StyleData::decode));

    public static final Codec<StyleData> CODEC_EQUIPPED = RecordCodecBuilder.create(instance -> instance.group(
            CODEC_PAIR.optionalFieldOf("head").forGetter(styleData -> getIdIntPair(styleData, Category.Head)),
            CODEC_PAIR.optionalFieldOf("body").forGetter(styleData -> getIdIntPair(styleData, Category.Body)),
            CODEC_PAIR.optionalFieldOf("legs").forGetter(styleData -> getIdIntPair(styleData, Category.Legs)),
            CODEC_PAIR.optionalFieldOf("feet").forGetter(styleData -> getIdIntPair(styleData, Category.Feet)),
            Codec.BOOL.optionalFieldOf("show_armor").forGetter(styleData -> Optional.of(styleData.isArmorVisible()))
    ).apply(instance, StyleData::decode));

    private static Optional<Pair<ResourceLocation, Integer>> getIdIntPair(StyleData styleData, Category slot) {
        var pair = styleData.getStyleForSlot(slot);
        if (pair.isPresent() && pair.get().getFirst() != null) {
            Style style = pair.get().getFirst();
            int texture = pair.get().getSecond();

            return Optional.of(Pair.of(style.getStyleId(), texture));
        }
        return Optional.empty();
    }
}

