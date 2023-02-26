package dev.bsmp.bouncestyles;

import com.google.common.io.Files;
import com.google.gson.*;
import dev.bsmp.bouncestyles.data.Style;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class StyleLoader {
    public static final HashMap<ResourceLocation, Style> REGISTRY = new HashMap<>();

    public static final ResourceLocation HEAD_ICON = new ResourceLocation(BounceStyles.modId, "textures/icon/bounce_head.png");
    public static final ResourceLocation BODY_ICON = new ResourceLocation(BounceStyles.modId, "textures/icon/bounce_body.png");
    public static final ResourceLocation LEGS_ICON = new ResourceLocation(BounceStyles.modId, "textures/icon/bounce_legs.png");
    public static final ResourceLocation FEET_ICON = new ResourceLocation(BounceStyles.modId, "textures/icon/bounce_feet.png");

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public static void init() throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Path dir = FabricLoader.getInstance().getGameDir().resolve("styles");
        if(!dir.toFile().mkdirs()) {
            File file = dir.resolve("styles.json").toFile();
            checkAndConvertOld(file);
            loadStyles(file);
        }
    }

    private static void loadStyles(File file) throws FileNotFoundException {
        BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8);
        JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);

        for (JsonElement element : jsonArray) {
            JsonObject item = GsonHelper.convertToJsonObject(element, "item");
            String name = item.get("name").getAsString();
            ResourceLocation styleId = new ResourceLocation(BounceStyles.modId, name);

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

    private static Style parseStyle(JsonObject item, ResourceLocation styleId) {
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
        Map<ResourceLocation, JsonObject> items = new HashMap<>();

        for(Category category : Category.values()) {
            File file = parentDir.resolve(category.name()+".json").toFile();
            if(file.exists()) {
                BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8);
                JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);
                reader.close();

                for (JsonElement element : jsonArray) {
                    JsonObject item = GsonHelper.convertToJsonObject(element, "item");
                    String name = item.get("name").getAsString();
                    ResourceLocation styleId = new ResourceLocation(BounceStyles.modId, name);

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

    public static Style getStyle(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static boolean idExists(ResourceLocation id) {
        return REGISTRY.containsKey(id);
    }

    private static ResourceLocation parseModelId(JsonObject item, String name) {
        String model;
        if(item.has("model_id"))
            model = item.get("model_id").getAsString();
        else
            model = name + ".geo.json";
        return model.contains(":") ? new ResourceLocation(model.split(":")[0], "geo/" + model.split(":")[1]) : new ResourceLocation(BounceStyles.modId, "geo/" + model);
    }

    private static ResourceLocation parseTextureId(JsonObject item, String name) {
        String texture;
        if(item.has("texture_id"))
            texture = item.get("texture_id").getAsString();
        else
            texture = name + ".png";
        return texture.contains(":") ? new ResourceLocation(texture.split(":")[0], "textures/" + texture.split(":")[1]) : new ResourceLocation(BounceStyles.modId, "textures/" + texture);
    }

    private static ResourceLocation parseAnimationId(JsonObject item, String name) {
        if(!item.has("animations"))
            return null;

        String anim;
        if(item.has("animation_id"))
            anim = item.get("animation_id").getAsString();
        else
            anim = name + ".animation.json";
        return anim.contains(":") ? new ResourceLocation(anim.split(":")[0], "animations/" + anim.split(":")[1]) : new ResourceLocation(BounceStyles.modId, "animations/" + anim);
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
        Head(HEAD_ICON), Body(BODY_ICON), Legs(LEGS_ICON), Feet(FEET_ICON);

        public final ResourceLocation categoryIcon;

        Category(ResourceLocation categoryIcon) {
            this.categoryIcon = categoryIcon;
        }
    }
}
