package dev.bsmp.bouncestyles;

import com.google.common.io.Files;
import com.google.gson.*;
import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StyleMagazineItem;
import dev.bsmp.bouncestyles.data.StylePreset;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class StyleLoader {
    public static final HashMap<Identifier, Style> REGISTRY = new HashMap<>();
    public static final HashMap<Identifier, StylePreset> PRESETS = new HashMap<>();

    public static final Identifier HEAD_ICON = new Identifier(BounceStyles.modId, "textures/icon/bounce_head.png");
    public static final Identifier BODY_ICON = new Identifier(BounceStyles.modId, "textures/icon/bounce_body.png");
    public static final Identifier LEGS_ICON = new Identifier(BounceStyles.modId, "textures/icon/bounce_legs.png");
    public static final Identifier FEET_ICON = new Identifier(BounceStyles.modId, "textures/icon/bounce_feet.png");
    public static final Identifier PRESET_ICON = new Identifier(BounceStyles.modId, "textures/icon/bounce_preset.png");

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public static void init() throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Path dir = FabricLoader.getInstance().getGameDir().resolve("styles");
        if(!dir.toFile().mkdirs()) {
            File file = dir.resolve("styles.json").toFile();
            checkAndConvertOld(file);
            loadStyles(file);
            loadPresets(dir.resolve("presets.json").toFile());
        }
    }

    private static void loadStyles(File file) throws FileNotFoundException {
        BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8);
        JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);

        for (JsonElement element : jsonArray) {
            JsonObject item = JsonHelper.asObject(element, "item");
            String name = item.get("name").getAsString();
            Identifier styleId = new Identifier(BounceStyles.modId, name);

            Style style = parseStyle(item, styleId);

            if (item.has("slots")) {
                for (JsonElement e : item.getAsJsonArray("slots")) {
                    switch (e.getAsString().toLowerCase()) {
                        case "head" -> style.categories.add(Category.Head);
                        case "body" -> style.categories.add(Category.Body);
                        case "legs" -> style.categories.add(Category.Legs);
                        case "feet" -> style.categories.add(Category.Feet);
                    }
                }
            }

            REGISTRY.put(styleId, style);
        }
    }

    private static Style parseStyle(JsonObject item, Identifier styleId) {
        Style style = new Style(
            styleId,
            parseModelId(item, styleId.getPath()),
            parseTextureId(item, styleId.getPath()),
            parseAnimationId(item, styleId.getPath()),
            parseAnimationMap(item)
        );

        //Animation Transition Ticks
        style.transitionTicks = parseTransitionTicks(item);

        //Hidden Parts
        if (item.has("hidden_parts")) {
            JsonArray hidden_parts = item.getAsJsonArray("hidden_parts");
            for (JsonElement e : hidden_parts) {
                String part = e.getAsString();
                if (!style.hiddenParts.contains(part))
                    style.hiddenParts.add(part);
            }
        }

        return style;
    }

    private static void checkAndConvertOld(File mainFile) throws IOException {
        Path parentDir = mainFile.getParentFile().toPath();
        Map<Identifier, JsonObject> items = new HashMap<>();

        for(Category category : Category.values()) {
            if(category == Category.Preset)
                continue;
            File file = parentDir.resolve(category.name()+".json").toFile();
            if(file.exists()) {
                BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8);
                JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);
                reader.close();

                for (JsonElement element : jsonArray) {
                    JsonObject item = JsonHelper.asObject(element, "item");
                    String name = item.get("name").getAsString();
                    Identifier styleId = new Identifier(BounceStyles.modId, name);

                    if(items.containsKey(styleId)) {
                        JsonObject obj = items.get(styleId);
                        JsonArray slots = obj.getAsJsonArray("slots");
                        slots.add(category.name().toLowerCase());
                        obj.add("slots", slots);
                    }
                    else {
                        JsonArray slots = new JsonArray();
                        slots.add(category.name().toLowerCase());
                        item.add("slots", slots);
                        items.put(styleId, item);
                    }
                }

                file.delete();
            }
        }

        if(!items.isEmpty()) {
            JsonArray array;
            if(mainFile.exists()) {
                    BufferedReader reader = Files.newReader(mainFile, StandardCharsets.UTF_8);
                    array = GSON.fromJson(reader, JsonArray.class);
                    reader.close();
            }
            else
                array = new JsonArray();

            for(JsonObject obj : items.values())
                array.add(obj);

            BufferedWriter bufferedWriter = Files.newWriter(mainFile, StandardCharsets.UTF_8);
            GSON.toJson(array, bufferedWriter);
            bufferedWriter.close();
        }
    }

    private static void loadPresets(File file) throws IOException {
        if(file.exists()) {
            BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8);
            JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);
            for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                Identifier presetId = Identifier.tryParse(BounceStyles.modId + ":" + entry.getKey());
                StylePreset preset = StylePreset.fromJson(presetId, entry.getValue().getAsJsonObject());
                PRESETS.put(presetId, preset);
            }
        }
    }

    public static StylePreset createPreset(StyleData styleData, String presetName) {
        StylePreset newPreset = styleData.createPreset(presetName);
        PRESETS.put(newPreset.presetId(), newPreset);
        writePresetsFile();
        return newPreset;
    }

    public static void removePreset(Identifier presetId) {
        PRESETS.remove(presetId);
        writePresetsFile();
    }

    private static void writePresetsFile() {
        Path dir = FabricLoader.getInstance().getGameDir().resolve("styles");
        File file = dir.resolve("presets.json").toFile();
        try {
            BufferedWriter bufferedWriter = Files.newWriter(file, StandardCharsets.UTF_8);
            JsonObject jsonObject = new JsonObject();

            for(StylePreset preset : PRESETS.values()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("name", preset.name());
                obj.addProperty("head", preset.headId() != null ? preset.headId().toString() : "");
                obj.addProperty("body", preset.bodyId() != null ? preset.bodyId().toString() : "");
                obj.addProperty("legs", preset.legsId() != null ? preset.legsId().toString() : "");
                obj.addProperty("feet", preset.feetId() != null ? preset.feetId().toString() : "");
                jsonObject.add(preset.presetId().getPath(), obj);
            }

            GSON.toJson(jsonObject, bufferedWriter);
            bufferedWriter.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Style getStyle(Identifier id) {
        return REGISTRY.get(id);
    }

    @Nullable public static Style getStyleFromStack(ItemStack itemStack) {
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

    public static boolean idExists(Identifier id) {
        return REGISTRY.containsKey(id);
    }

    private static Identifier parseModelId(JsonObject item, String name) {
        String model;
        if(item.has("model_id"))
            model = item.get("model_id").getAsString();
        else
            model = name + ".geo.json";
        return model.contains(":") ? new Identifier(model.split(":")[0], "geo/" + model.split(":")[1]) : new Identifier(BounceStyles.modId, "geo/" + model);
    }

    private static Identifier parseTextureId(JsonObject item, String name) {
        String texture;
        if(item.has("texture_id"))
            texture = item.get("texture_id").getAsString();
        else
            texture = name + ".png";
        return texture.contains(":") ? new Identifier(texture.split(":")[0], "textures/" + texture.split(":")[1]) : new Identifier(BounceStyles.modId, "textures/" + texture);
    }

    private static Identifier parseAnimationId(JsonObject item, String name) {
        if(!item.has("animations"))
            return null;

        String anim;
        if(item.has("animation_id"))
            anim = item.get("animation_id").getAsString();
        else
            anim = name + ".animation.json";
        return anim.contains(":") ? new Identifier(anim.split(":")[0], "animations/" + anim.split(":")[1]) : new Identifier(BounceStyles.modId, "animations/" + anim);
    }

    private static HashMap<String, String> parseAnimationMap(JsonObject item) {
        if(!item.has("animations"))
            return null;

        HashMap<String, String> animationMap = new HashMap<>();
        JsonObject animations = item.getAsJsonObject("animations");
        for(Map.Entry<String, JsonElement> entry : animations.entrySet()) {
            String a = entry.getValue().getAsString();
            if(!a.isEmpty())
                animationMap.put(entry.getKey(), entry.getValue().getAsString());
        }
        return animationMap;
    }

    private static int parseTransitionTicks(JsonObject item) {
        return item.has("transition_ticks") ? item.get("transition_ticks").getAsInt() : 5;
    }

    public enum Category {
        Head(HEAD_ICON), Body(BODY_ICON), Legs(LEGS_ICON), Feet(FEET_ICON), Preset(PRESET_ICON);

        public final Identifier categoryIcon;

        Category(Identifier categoryIcon) {
            this.categoryIcon = categoryIcon;
        }
    }
}
