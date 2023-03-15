package dev.bsmp.bouncestyles.data;

import com.google.gson.JsonObject;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.util.Identifier;

public class StylePreset{
    public Identifier presetId;
    public String name;
    public @Nullable Identifier headId;
    public @Nullable Identifier bodyId;
    public @Nullable Identifier legsId;
    public @Nullable Identifier feetId;
    public boolean error;

    public StylePreset(Identifier presetId, String name, @Nullable Identifier headId, @Nullable Identifier bodyId, @Nullable Identifier legsId, @Nullable Identifier feetId, boolean error){
        this.presetId = presetId;
        this.name = name;
        this.headId = headId;
        this.bodyId = bodyId;
        this.legsId = legsId;
        this.feetId = feetId;
        this.error = error;
    }

    public static StylePreset fromJson(Identifier presetId, JsonObject json) {
        String name = json.get("name").getAsString();
        Identifier head = parseId(json.get("head").getAsString());
        Identifier body = parseId(json.get("body").getAsString());
        Identifier legs = parseId(json.get("legs").getAsString());
        Identifier feet = parseId(json.get("feet").getAsString());
        boolean error = checkIds(head, body, legs, feet);
        return new StylePreset(presetId, name, head, body, legs, feet, error);
    }

    private static Identifier parseId(String string) {
        return string.contains(":") ? new Identifier(string) : new Identifier(BounceStyles.modId, string);
    }

    public boolean hasAllUnlocked(List<Identifier> unlocks) {
        return unlocks.contains(headId) && unlocks.contains(bodyId) && unlocks.contains(legsId) && unlocks.contains(feetId);
    }

    public boolean hasAllUnlocked(StyleData styleData) {
        return styleData.hasStyleUnlocked(headId) && styleData.hasStyleUnlocked(bodyId) && styleData.hasStyleUnlocked(legsId) && styleData.hasStyleUnlocked(feetId);
    }

    public static boolean checkIds(Identifier... ids) {
        boolean flag = false;
        for(Identifier id : ids) {
            if(id != null && !StyleLoader.idExists(id))
                flag = true;
        }
        return flag;
    }

}