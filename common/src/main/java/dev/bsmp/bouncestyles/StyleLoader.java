package dev.bsmp.bouncestyles;

import com.google.common.io.Files;
import com.google.gson.*;
import dev.architectury.platform.Platform;
import dev.bsmp.bouncestyles.data.Style;
import dev.bsmp.bouncestyles.data.StylePreset;
import dev.bsmp.bouncestyles.pack.StylesResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class StyleLoader {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public static void init() {
        try {
            Path dir = Platform.getGameFolder().resolve("styles");
            File styleFile = dir.resolve("styles.json").toFile();
            File presetsFile = dir.resolve("presets.json").toFile();

            if(dir.toFile().mkdirs() || !presetsFile.exists())
                createPresetFile(presetsFile);

            checkAndConvertOld(styleFile);
            if(styleFile.exists())
                loadStyles(styleFile);

            loadPresets(presetsFile);
        }
        catch (IOException e) {
            BounceStyles.LOGGER.error(e);
        }
    }

    public static void reload() {
        StyleRegistry.clearRegistry();
        init();
    }

    private static void createPresetFile(File presetsFile) throws IOException {
        if(!presetsFile.exists())
            try(BufferedWriter writer = Files.newWriter(presetsFile, StandardCharsets.UTF_8)) {
                GSON.toJson(new JsonObject(), writer);
            }
    }

    public static void loadStyles(File file) throws IOException {
        if(!file.exists())
            return;

        try(BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8)) {
            loadStyles("Root styles.json", reader);
        }
    }

    public static void loadStyles(String fileName, InputStream stream) {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            loadStyles(fileName, reader);
        } catch (IOException e) {
            BounceStyles.LOGGER.error(e);
        }
    }

    private static void loadStyles(String fileName, BufferedReader reader) {
        try {
            JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);

            if (jsonArray == null) {
                BounceStyles.LOGGER.warn("Read an Empty or Invalid Json file [\"" + fileName + "\"]; Skipping...");
                return;
            }

            int i = 0;
            for (JsonElement element : jsonArray) {
                JsonObject item = JsonHelper.asObject(element, "item");
                String name = item.get("name").getAsString();
                Identifier styleId = new Identifier(BounceStyles.modId, name);

                Style style = parseStyle(item, styleId);

                if (item.has("slots")) {
                    for (JsonElement e : item.getAsJsonArray("slots")) {
                        switch (e.getAsString().toLowerCase()) {
                            case "head" -> style.categories.add(StyleRegistry.Category.Head);
                            case "body" -> style.categories.add(StyleRegistry.Category.Body);
                            case "legs" -> style.categories.add(StyleRegistry.Category.Legs);
                            case "feet" -> style.categories.add(StyleRegistry.Category.Feet);
                        }
                    }
                }

                StyleRegistry.registerStyle(styleId, style);
                i++;
            }

            BounceStyles.LOGGER.info("Added " + i + " styles from \"" + fileName +"\"");
        }
        catch (JsonParseException e) {
            BounceStyles.LOGGER.error("Ran into issues parsing json! - " + e.getLocalizedMessage());
            BounceStyles.LOGGER.error(e);
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

        for(StyleRegistry.Category category : StyleRegistry.Category.values()) {
            if(category == StyleRegistry.Category.Preset)
                continue;

            File file = parentDir.resolve(category.name()+".json").toFile();
            if(file.exists()) {
                try(BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8)) {
                    JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);

                    for (JsonElement element : jsonArray) {
                        JsonObject item = JsonHelper.asObject(element, "item");
                        String name = item.get("name").getAsString();
                        Identifier styleId = new Identifier(BounceStyles.modId, name);

                        if (items.containsKey(styleId)) {
                            JsonObject obj = items.get(styleId);
                            JsonArray slots = obj.getAsJsonArray("slots");
                            slots.add(category.name().toLowerCase());
                            obj.add("slots", slots);
                        } else {
                            JsonArray slots = new JsonArray();
                            slots.add(category.name().toLowerCase());
                            item.add("slots", slots);
                            items.put(styleId, item);
                        }
                    }

                    file.delete();
                }
            }
        }

        if(!items.isEmpty()) {
            JsonArray array;
            if(mainFile.exists()) {
                try(BufferedReader reader = Files.newReader(mainFile, StandardCharsets.UTF_8)) {
                    array = GSON.fromJson(reader, JsonArray.class);
                }
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
            try(BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8)) {
                JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);
                if(jsonObject == null)
                    return;

                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    Identifier presetId = Identifier.tryParse(BounceStyles.modId + ":" + entry.getKey());
                    StylePreset preset = StylePreset.fromJson(presetId, entry.getValue().getAsJsonObject());
                    StyleRegistry.PRESETS.put(presetId, preset);
                }
            }
        }
    }

    public static void removePreset(Identifier presetId) {
        StyleRegistry.PRESETS.remove(presetId);
        writePresetsFile();
    }

    public static void writePresetsFile() {
        Path dir = Platform.getGameFolder().resolve("styles");
        File file = dir.resolve("presets.json").toFile();
        try {
            BufferedWriter bufferedWriter = Files.newWriter(file, StandardCharsets.UTF_8);
            JsonObject jsonObject = new JsonObject();

            for(StylePreset preset : StyleRegistry.PRESETS.values()) {
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

    public static CompletableFuture<Void> loadStylePacks(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        BounceStyles.LOGGER.info("Registering styles from Style Packs...");
        return CompletableFuture.supplyAsync(() -> {
            StyleLoader.reload();
            for(ResourcePack pack : resourceManager.streamResourcePacks().toList()) {
                if(pack instanceof StylesResourcePack) {
                    ((StylesResourcePack) pack).registerPackStyles();
                }
            }
            return null;
        }, prepareExecutor)
        .thenCompose(synchronizer::whenPrepared)
        .thenAcceptAsync(o -> {}, applyExecutor);
    }
}
