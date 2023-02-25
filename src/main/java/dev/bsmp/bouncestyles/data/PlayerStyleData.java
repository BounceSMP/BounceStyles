package dev.bsmp.bouncestyles.data;

import dev.bsmp.bouncestyles.GarmentLoader;
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
    private @Nullable Garment headGarment;
    private @Nullable Garment bodyGarment;
    private @Nullable Garment legGarment;
    private @Nullable Garment feetGarment;
    private List<ResourceLocation> unlocks;
    private List<String> hiddenParts = new ArrayList<>();

    public PlayerStyleData(@Nullable Garment headGarment, @Nullable Garment bodyGarment, @Nullable Garment legGarment, @Nullable Garment feetGarment) {
        this(headGarment, bodyGarment, legGarment, feetGarment, new ArrayList<>());
    }

    public PlayerStyleData(@Nullable Garment headGarment, @Nullable Garment bodyGarment, @Nullable Garment legGarment, @Nullable Garment feetGarment, List<ResourceLocation> unlocks) {
        setHeadGarment(headGarment);
        setBodyGarment(bodyGarment);
        setLegGarment(legGarment);
        setFeetGarment(feetGarment);
        setUnlocks(unlocks);
    }

    public void setHeadGarment(Garment headGarment) {
        this.headGarment = headGarment;
        updateVisibility(headGarment);
    }
    public void setBodyGarment(Garment bodyGarment) {
        this.bodyGarment = bodyGarment;
        updateVisibility(bodyGarment);
    }
    public void setLegGarment(Garment legGarment) {
        this.legGarment = legGarment;
        updateVisibility(legGarment);
    }
    public void setFeetGarment(Garment feetGarment) {
        this.feetGarment = feetGarment;
        updateVisibility(feetGarment);
    }

    private void updateVisibility(Garment garment) {
        if(garment == null || garment.hiddenParts == null)
            return;
        for(String s : garment.hiddenParts) {
            if(!this.hiddenParts.contains(s))
                this.hiddenParts.add(s);
        }
    }
    public List<String> getHiddenParts() {
        return this.hiddenParts;
    }

    public Garment getHeadGarment() {
        return headGarment;
    }
    public Garment getBodyGarment() {
        return bodyGarment;
    }
    public Garment getLegGarment() {
        return legGarment;
    }
    public Garment getFeetGarment() {
        return feetGarment;
    }

    public Garment getGarmentForSlot(GarmentLoader.Category category) {
        return switch (category) {
            case Head -> this.headGarment;
            case Body -> this.bodyGarment;
            case Legs -> this.legGarment;
            case Feet -> this.feetGarment;
        };
    }

    public List<ResourceLocation> getUnlocks() {
        return this.unlocks;
    }
    public void setUnlocks(List<ResourceLocation> unlocks) {
        this.unlocks = unlocks;
    }
    public boolean unlockGarment(Garment garment) {
        return unlockGarment(garment.garmentId);
    }
    public boolean unlockGarment(ResourceLocation garmentId) {
        if(unlocks.contains(garmentId))
            return false;
        return unlocks.add(garmentId);
    }
    public boolean hasGarmentUnlocked(Garment garment) {
        return hasGarmentUnlocked(garment.garmentId);
    }
    public boolean hasGarmentUnlocked(ResourceLocation id) {
        return unlocks.contains(id);
    }

    //Static
    public static void setPlayerData(Player player, PlayerStyleData styleData) {
        ((StyleEntity)player).setStyleData(styleData);
    }

    public static PlayerStyleData getPlayerData(Player player) {
        return ((StyleEntity)player).getStyleData();
    }

    private static void convertGarment(CompoundTag tag, Garment garment, String slot) {
        if(garment != null)
            tag.putString(slot, garment.garmentId.toString());
    }

    private static @Nullable Garment parseGarment(CompoundTag tag, String slot) {
        Garment garment = null;
        if(tag.contains(slot))
            garment = GarmentLoader.Category.valueOf(slot).entryList.get(ResourceLocation.tryParse(tag.getString(slot)));
        return garment;
    }

    public static CompoundTag toNBT(PlayerStyleData styleData) {
        CompoundTag tag = equippedToNBT(styleData);
        tag.put("unlocks", unlocksToNBT(styleData));
        return tag;
    }

    public static CompoundTag equippedToNBT(PlayerStyleData styleData) {
        CompoundTag tag = new CompoundTag();
        convertGarment(tag, styleData.headGarment, GarmentLoader.Category.Head.name());
        convertGarment(tag, styleData.bodyGarment, GarmentLoader.Category.Body.name());
        convertGarment(tag, styleData.legGarment, GarmentLoader.Category.Legs.name());
        convertGarment(tag, styleData.feetGarment, GarmentLoader.Category.Feet.name());
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
                parseGarment(tag, GarmentLoader.Category.Head.name()),
                parseGarment(tag, GarmentLoader.Category.Body.name()),
                parseGarment(tag, GarmentLoader.Category.Legs.name()),
                parseGarment(tag, GarmentLoader.Category.Feet.name())
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
