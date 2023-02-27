package dev.bsmp.bouncestyles.data;

import com.google.gson.JsonObject;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record StylePreset(ResourceLocation presetId, String name, @Nullable ResourceLocation headId, @Nullable ResourceLocation bodyId, @Nullable ResourceLocation legsId, @Nullable ResourceLocation feetId, boolean error) {

    public static StylePreset fromJson(ResourceLocation presetId, JsonObject json) {
        String name = json.get("name").getAsString();
        ResourceLocation head = parseId(json.get("head").getAsString());
        ResourceLocation body = parseId(json.get("body").getAsString());
        ResourceLocation legs = parseId(json.get("legs").getAsString());
        ResourceLocation feet = parseId(json.get("feet").getAsString());
        boolean error = checkIds(head, body, legs, feet);
        return new StylePreset(presetId, name, head, body, legs, feet, error);
    }

    private static ResourceLocation parseId(String string) {
        return string.contains(":") ? new ResourceLocation(string) : new ResourceLocation(BounceStyles.modId, string);
    }

    public boolean hasAllUnlocked(List<ResourceLocation> unlocks) {
        return unlocks.contains(headId) && unlocks.contains(bodyId) && unlocks.contains(legsId) && unlocks.contains(feetId);
    }

    public boolean hasAllUnlocked(PlayerStyleData styleData) {
        return styleData.hasStyleUnlocked(headId) && styleData.hasStyleUnlocked(bodyId) && styleData.hasStyleUnlocked(legsId) && styleData.hasStyleUnlocked(feetId);
    }

    public static boolean checkIds(ResourceLocation... ids) {
        boolean flag = false;
        for(ResourceLocation id : ids) {
            if(id != null && !StyleLoader.idExists(id))
                flag = true;
        }
        return flag;
    }

}