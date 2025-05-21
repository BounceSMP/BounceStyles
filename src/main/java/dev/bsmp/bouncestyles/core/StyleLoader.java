package dev.bsmp.bouncestyles.core;

import com.google.common.io.Files;
import com.google.gson.*;
import dev.bsmp.bouncestyles.core.data.StylePreset;
import dev.bsmp.bouncestyles.core.pack.StylesResourcePack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

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
            //ToDo Moving to Dynamic Registries means this stuff changes a lot
            Path dir = getStylesDirectory();
            File rootStylesFile = dir.resolve("styles.json").toFile();
            File presetsFile = dir.resolve("presets.json").toFile();

            if(dir.toFile().mkdirs() || !presetsFile.exists())
                createPresetFile(presetsFile);

            checkAndConvertOld(rootStylesFile);
            if(rootStylesFile.exists())
                loadStyles(rootStylesFile);

            loadPresets(presetsFile);
        }
        catch (IOException e) {
            BounceStyles.LOGGER.error(e);
        }
    }

    public static void reload() {
        BounceStylesRegistries.clearRegistry();
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
                BounceStyles.LOGGER.warn("Read an Empty or Invalid Json file [\"{}\"]; Skipping...", fileName);
                return;
            }

            int i = 0;
            for (JsonElement element : jsonArray) {
                JsonObject item = GsonHelper.convertToJsonObject(element, "item");
                String name = item.get("name").getAsString();
                ResourceLocation styleId = BounceStyles.resourceLocation(name);

//                Style.CODEC.parse(JsonOps.INSTANCE, element).resultOrPartial(BounceStyles.LOGGER::error).ifPresent(style -> {
//                    var registry = BounceStyles.getLevel().registryAccess().registry(BounceStyles.STYLE_REGISTRY_KEY);
//                    Registry.register(registry.get(), new ResourceLocation("tets"), style);
//                });

//                StyleRegistry.registerStyle(styleId, style);
                i++;
            }

            BounceStyles.LOGGER.info("Added {} styles from \"{}\"", i, fileName);
        }
        catch (JsonParseException e) {
            BounceStyles.LOGGER.error("Ran into issues parsing json! - {}", e.getLocalizedMessage());
            BounceStyles.LOGGER.error(e);
        }
    }

    private static void checkAndConvertOld(File mainFile) throws IOException {
        Path parentDir = mainFile.getParentFile().toPath();
        Map<ResourceLocation, JsonObject> items = new HashMap<>();

        for(BounceStylesRegistries.Category category : BounceStylesRegistries.Category.values()) {
            if(category == BounceStylesRegistries.Category.Preset)
                continue;

            File file = parentDir.resolve(category.name()+".json").toFile();
            if(file.exists()) {
                try(BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8)) {
                    JsonArray jsonArray = GSON.fromJson(reader, JsonArray.class);

                    for (JsonElement element : jsonArray) {
                        JsonObject item = GsonHelper.convertToJsonObject(element, "item");
                        String name = item.get("name").getAsString();
                        ResourceLocation styleId = BounceStyles.resourceLocation(name);

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
                    ResourceLocation presetId = ResourceLocation.tryParse(BounceStyles.modId + ":" + entry.getKey());
                    StylePreset preset = StylePreset.fromJson(presetId, entry.getValue().getAsJsonObject());
                    BounceStylesRegistries.PRESETS.put(presetId, preset);
                }
            }
        }
    }

    public static void removePreset(ResourceLocation presetId) {
        BounceStylesRegistries.PRESETS.remove(presetId);
        writePresetsFile();
    }

    public static void writePresetsFile() {
        Path dir = getStylesDirectory();
        File file = dir.resolve("presets.json").toFile();
        try {
            BufferedWriter bufferedWriter = Files.newWriter(file, StandardCharsets.UTF_8);
            JsonObject jsonObject = new JsonObject();

            for(StylePreset preset : BounceStylesRegistries.PRESETS.values()) {
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

    public static CompletableFuture<Void> loadStylePacks(PreparableReloadListener.PreparationBarrier synchronizer, ResourceManager resourceManager, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        BounceStyles.LOGGER.info("Registering styles from Style Packs...");
        return CompletableFuture.supplyAsync(() -> {
            StyleLoader.reload();
            for(PackResources pack : resourceManager.listPacks().toList()) {
                if(pack instanceof StylesResourcePack) {
                    ((StylesResourcePack) pack).registerPackStyles();
                }
            }
            return null;
        }, prepareExecutor)
        .thenCompose(synchronizer::wait)
        .thenAcceptAsync(o -> {}, applyExecutor);
    }

    public static Path getStylesDirectory() {
        Path dir;
        //? if neoforge {
        /*dir = net.neoforged.fml.loading.FMLPaths.GAMEDIR.get().resolve("styles");
         *///?} else if forge {
        dir = net.minecraftforge.fml.loading.FMLPaths.GAMEDIR.get().resolve("styles");
         //?} else if fabric {
        /*dir = net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir().resolve("styles");
        *///?}
        return dir;
    }
}
