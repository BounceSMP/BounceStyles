package dev.bsmp.bouncestyles;

import com.google.common.io.Files;
import com.google.gson.*;
import dev.bsmp.bouncestyles.data.Garment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class GarmentLoader {
    public static final List<ResourceLocation> UNIQUE_IDS = new ArrayList<>();
    public static final Map<ResourceLocation, Garment> HEAD_ITEMS = new HashMap<>();
    public static final Map<ResourceLocation, Garment> BODY_ITEMS = new HashMap<>();
    public static final Map<ResourceLocation, Garment> LEGS_ITEMS = new HashMap<>();
    public static final Map<ResourceLocation, Garment> FEET_ITEMS = new HashMap<>();

    public static final ResourceLocation HEAD_ICON = new ResourceLocation(BounceStyles.modId, "textures/icon/bounce_head.png");
    public static final ResourceLocation BODY_ICON = new ResourceLocation(BounceStyles.modId, "textures/icon/bounce_body.png");
    public static final ResourceLocation LEGS_ICON = new ResourceLocation(BounceStyles.modId, "textures/icon/bounce_legs.png");
    public static final ResourceLocation FEET_ICON = new ResourceLocation(BounceStyles.modId, "textures/icon/bounce_feet.png");

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public static void init() throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Path dir = FabricLoader.getInstance().getGameDir().resolve("styles");
        dir.toFile().mkdirs();
        for(Category category : Category.values()) {
            File file = dir.resolve(category.name()+".json").toFile();
            if(!file.exists()) {
                JsonArray jsonArray = new JsonArray();
                Objects.requireNonNull(jsonArray);
                BufferedWriter bufferedWriter = Files.newWriter(file, StandardCharsets.UTF_8);
                GSON.toJson(jsonArray, bufferedWriter);
                bufferedWriter.close();
            }
            else {
                BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8);
                JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);
                for(JsonElement element : jsonArray) {
                    JsonObject item = GsonHelper.convertToJsonObject(element, "item");
                    String name = item.get("name").getAsString();

                    ResourceLocation modelID = parseModel(item, name);
                    ResourceLocation textureID = parseTexture(item, name);
                    ResourceLocation animationID = parseAnimation(item, name);
                    HashMap<String, String> animationMap = parseAnimationMap(item);

                    //Animation Transition Ticks
                    int transitionTicks = item.has("transition_ticks") ? item.get("transition_ticks").getAsInt() : 5;

                    //Hidden Parts
                    List<String> parts = null;
                    if(item.has("hidden_parts")) {
                        parts = new ArrayList<>();
                        JsonArray hidden_parts = item.getAsJsonArray("hidden_parts");
                        for(JsonElement e : hidden_parts)
                            parts.add(e.getAsString());
                    }

                    ResourceLocation id = new ResourceLocation(BounceStyles.modId, name);
                    Garment garment = new Garment(id, modelID, textureID, animationID, animationMap);

                    garment.hiddenParts = parts;
                    garment.transitionTicks = transitionTicks;

                    category.entryList.put(id, garment);
                    if(!UNIQUE_IDS.contains(id))
                        UNIQUE_IDS.add(id);
                }
            }
        }
    }

    public static boolean idExists(ResourceLocation id) {
        return UNIQUE_IDS.contains(id);
    }

    private static ResourceLocation parseModel(JsonObject item, String name) {
        String model;
        if(item.has("model_id"))
            model = item.get("model_id").getAsString();
        else
            model = name + ".geo.json";
        return model.contains(":") ? new ResourceLocation(model.split(":")[0], "geo/" + model.split(":")[1]) : new ResourceLocation(BounceStyles.modId, "geo/" + model);
    }

    private static ResourceLocation parseTexture(JsonObject item, String name) {
        String texture;
        if(item.has("texture_id"))
            texture = item.get("texture_id").getAsString();
        else
            texture = name + ".png";
        return texture.contains(":") ? new ResourceLocation(texture.split(":")[0], "textures/" + texture.split(":")[1]) : new ResourceLocation(BounceStyles.modId, "textures/" + texture);
    }

    private static ResourceLocation parseAnimation(JsonObject item, String name) {
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

    public enum Category {
        Head(HEAD_ICON, HEAD_ITEMS), Body(BODY_ICON, BODY_ITEMS), Legs(LEGS_ICON, LEGS_ITEMS), Feet(FEET_ICON, FEET_ITEMS);

        public final Map<ResourceLocation, Garment> entryList;
        public final ResourceLocation categoryIcon;

        Category(ResourceLocation categoryIcon, Map<ResourceLocation, Garment> entryList) {
            this.categoryIcon = categoryIcon;
            this.entryList = entryList;
        }
    }
}
