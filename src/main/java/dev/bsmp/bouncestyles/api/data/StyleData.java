package dev.bsmp.bouncestyles.api.data;
//~ avatar

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.bsmp.bouncestyles.api.style.Category;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.api.style.StylePreset;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import dev.bsmp.bouncestyles.core.data.attached.StyleDataAttachment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EquipmentSlot;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class StyleData {
    private final Set<String> hiddenParts = new HashSet<>();
    private final Map<Category, EquippedStyle> equippedStyles = new HashMap<>(Map.of(
            Category.Head, new EquippedStyle(),
            Category.Body, new EquippedStyle(),
            Category.Legs, new EquippedStyle(),
            Category.Feet, new EquippedStyle()
    ));

    public StyleData() {}

    public StyleData(EquippedStyle head, EquippedStyle body, EquippedStyle legs, EquippedStyle feet) {
        this.equipStyle(Category.Head, head);
        this.equipStyle(Category.Body, body);
        this.equipStyle(Category.Legs, legs);
        this.equipStyle(Category.Feet, feet);
    }

    public boolean equipStyle(Category slot, @Nullable Identifier styleId) {
        return this.equipStyle(slot, new EquippedStyle(getStyleFromId(styleId).orElse(null)));
    }

    public boolean equipStyle(Category slot, @Nullable Identifier styleId, int variant) {
        var style = getStyleFromId(styleId).orElse(null);
        return this.equipStyle(slot, new EquippedStyle(style, variant));
    }

    public boolean equipStyle(Category slot, EquippedStyle equipped) {
        var style = equipped.getStyle().orElse(null);
        var variant = equipped.getVariant();

        if (style != null) {
            if (!style.getCategories().contains(slot)) return false;
            if (variant > -1 && (!style.hasVariants() || style.getTextureVariants().get().size() <= variant)) return false;
        }

        this.equippedStyles.put(slot, equipped);
        updatePartVisibility();
        return true;
    }

    private void updatePartVisibility() {
        this.hiddenParts.clear();

        this.equippedStyles.forEach((slot, data) ->
            data.getStyle().ifPresent(style -> {
                if (style.getHiddenParts().isEmpty()) return;
                this.hiddenParts.addAll(style.getHiddenParts().get());
            })
        );
    }

    public Map<Category, EquippedStyle> getAllNonEmpty() {
        return this.equippedStyles.entrySet().stream()
                .filter(entry -> entry.getValue().getStyle().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public EquippedStyle getStyleForSlot(Category category) {
        return this.equippedStyles.get(category);
    }

    public EquippedStyle getHeadStyle() {
        return this.equippedStyles.get(Category.Head);
    }

    public EquippedStyle getBodyStyle() {
        return this.equippedStyles.get(Category.Body);
    }

    public EquippedStyle getLegsStyle() {
        return this.equippedStyles.get(Category.Legs);
    }

    public EquippedStyle getFeetStyle() {
        return this.equippedStyles.get(Category.Feet);
    }

    public boolean isEquipmentSlotVisible(EquipmentSlot slot) {
        if (this.hiddenParts.contains("armor.*") || this.hiddenParts.contains("armor."+slot.getName().toLowerCase())) return false;
        return true;
    }

    public Set<String> getHiddenParts() {
        return this.hiddenParts;
    }

    public StylePreset createPreset() {
        return new StylePreset(
                this.getHeadStyle(),
                this.getBodyStyle(),
                this.getLegsStyle(),
                this.getFeetStyle()
        );
    }

    public static boolean hasStyleData(Avatar avatar) {
        return StyleDataAttachment.hasEntityData(avatar);
    }

    public static StyleData getEntityData(Avatar avatar) {
        return StyleDataAttachment.getEntityData(avatar);
    }

    public static void setEntityData(Avatar avatar, StyleData styleData) {
        StyleDataAttachment.setEntityData(avatar, styleData);
        if (avatar.level().isClientSide())
            BounceStylesClient.onStyleUpdate();
    }

    public static void copyFrom(Avatar oldAvatar, Avatar newAvatar) {
        StyleData styleData = StyleData.getEntityData(oldAvatar);
        StyleData.setEntityData(newAvatar, styleData);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(CODEC, this);
    }

    public static StyleData read(FriendlyByteBuf buf) {
        //~ if >= 1.21.8 'readJsonWithCodec' -> 'readLenientJsonWithCodec'
        return buf.readLenientJsonWithCodec(CODEC);
    }

    private static Optional<Style> getStyleFromId(@Nullable Identifier styleId) {
        if (styleId != null)
            return BounceStylesRegistries.getStyle(styleId);
        return Optional.empty();
    }

    public static final Codec<StyleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EquippedStyle.CODEC.fieldOf("head").forGetter(StyleData::getHeadStyle),
            EquippedStyle.CODEC.fieldOf("body").forGetter(StyleData::getBodyStyle),
            EquippedStyle.CODEC.fieldOf("legs").forGetter(StyleData::getLegsStyle),
            EquippedStyle.CODEC.fieldOf("feet").forGetter(StyleData::getFeetStyle)
    ).apply(instance, StyleData::new));

    public static final StreamCodec<FriendlyByteBuf, StyleData> STREAM_CODEC = StreamCodec.ofMember(StyleData::write, StyleData::read);
}

