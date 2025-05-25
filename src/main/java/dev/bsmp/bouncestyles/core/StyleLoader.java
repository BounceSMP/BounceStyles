package dev.bsmp.bouncestyles.core;

import com.google.common.io.Files;
import com.google.gson.*;
import dev.architectury.platform.Platform;
import dev.bsmp.bouncestyles.api.style.StylePreset;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class StyleLoader {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public static void checkAndConvertPackFormat() {
        File stylesDirectory = getStylesDirectory();
        if (stylesDirectory.mkdirs() || !stylesDirectory.isDirectory()) return;

        File[] zipFiles = stylesDirectory.listFiles((file, name) -> name.toLowerCase().endsWith(".zip"));
        if (zipFiles != null) {
            for (File zip : zipFiles) {
                if (unzipFile(zip))
                    if (!zip.delete())
                        BounceStyles.LOGGER.warn("Failed to delete zip file post-extraction: {}", zip.getName());
            }
        }

        File[] packs = stylesDirectory.listFiles(File::isDirectory);
        if (packs != null) {
            for (File dir : packs) {
                File stylesJson = new File(dir, "styles.json");
                if (stylesJson.exists() && stylesJson.isFile()) {
                    if (convertStyleJsonToData(dir, stylesJson))
                        if (!stylesJson.delete())
                            if (!stylesJson.renameTo(new File(stylesJson.getName()+".old")))
                                BounceStyles.LOGGER.warn("Failed to delete or rename styles.json file post-conversion in pack '{}'", dir.getName());
                }
            }
        }

        File rootStyles = new File(stylesDirectory, "styles.json");
        if (rootStyles.exists()) {
            rootStyles.renameTo(new File("styles.json.old"));
            BounceStyles.LOGGER.warn("styles.json file found in the Styles folder, but this method is no longer functional. Please make an actual Style Pack!");
        }
    }

    private static boolean unzipFile(File zipFile) {
        try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            String basePath = "";
            Map<String, ByteArrayOutputStream> entries = new LinkedHashMap<>();

            while ((entry = inputStream.getNextEntry()) != null) {
                ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
                if (!entry.isDirectory()) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = inputStream.read(buffer)) > 0) {
                        bufferStream.write(buffer, 0, len);
                    }
                }

                String name = entry.getName();
                entries.put(name, bufferStream);
                if (name.toLowerCase().endsWith("styles.json") || name.toLowerCase().endsWith("pack.mcmeta")) {
                    int idx = name.lastIndexOf("/");
                    if (idx >= 0) basePath = name.substring(0, idx + 1);
                    else basePath = "";
                    break;
                }

                inputStream.closeEntry();
            }

            File outputDir = getStylesDirectory();
            String zipBaseName = zipFile.getName().replace(".zip", "");
            File targetBaseDir = new File(outputDir, zipBaseName);
            int suffix = 1;
            while (targetBaseDir.exists() && !targetBaseDir.isDirectory()) {
                targetBaseDir = new File(outputDir, zipBaseName + "_" + suffix);
                suffix++;
            }
            targetBaseDir.mkdirs();

            for (Map.Entry<String, ByteArrayOutputStream> e : entries.entrySet()) {
                String entryName = e.getKey();
                if (!entryName.startsWith(basePath)) continue;
                String relativePath = entryName.substring(basePath.length());
                File outFile = new File(targetBaseDir, relativePath);

                if (entryName.endsWith("/")) {
                    outFile.mkdirs();
                } else {
                    File parent = outFile.getParentFile();
                    if (parent != null) parent.mkdirs();
                    try (FileOutputStream outputStream = new FileOutputStream(outFile)) {
                        e.getValue().writeTo(outputStream);
                    }
                }
            }

            return true;
        }
        catch (Exception e) {
            BounceStyles.LOGGER.error("Exception Occurred trying to unzip file: {}", zipFile.getName(), e);
        }
        return false;
    }

    private static boolean convertStyleJsonToData(File directory, File jsonFile) {
        try (BufferedReader reader = Files.newReader(jsonFile, StandardCharsets.UTF_8)) {
            var jsonArray = GSON.fromJson(reader, JsonArray.class);
            for (JsonElement obj : jsonArray) {
                JsonObject styleObj = obj.getAsJsonObject();
                String name = styleObj.get("name").getAsString();
                File styleFile = new File(directory, "data/" + styleIdToPath(name));
                styleFile.getParentFile().mkdirs();

                try (Writer writer = new FileWriter(styleFile)) {
                    GSON.toJson(styleObj, writer);
                }
                catch (Exception e) {
                    BounceStyles.LOGGER.error("Exception Occurred trying to write json for Style: '{}' in pack: '{}'", name, directory.getName(), e);
                }
            }
        }
        catch (Exception e) {
            BounceStyles.LOGGER.error("Exception Occurred trying to convert old styles.json format for pack: {}", directory.getName(), e);
        }
        return false;
    }

    private static String styleIdToPath(String name) {
        ResourceLocation registryId = BounceStylesRegistries.STYLE_REGISTRY_KEY.location();
        String nameSpace = name.contains(":") ? name.split(":")[0] : BounceStyles.modId;
        String path = name.contains(":") ? name.split(":")[1] : name;
        return nameSpace + "/" + registryId.getNamespace() + "/" + registryId.getPath() + "/" + path + ".json";
    }

    //ToDo Move Preset loading to somewhere new
    private static void createPresetFile(File presetsFile) throws IOException {
        if(!presetsFile.exists())
            try(BufferedWriter writer = Files.newWriter(presetsFile, StandardCharsets.UTF_8)) {
                GSON.toJson(new JsonObject(), writer);
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
                    StylePreset.fromJson(presetId, entry.getValue().getAsJsonObject()).ifPresent(stylePreset -> BounceStylesRegistries.PRESETS.put(presetId, stylePreset));
                }
            }
        }
    }

    public static void removePreset(ResourceLocation presetId) {
        BounceStylesRegistries.PRESETS.remove(presetId);
        writePresetsFile();
    }

    public static void writePresetsFile() {
        File dir = getStylesDirectory();
        File file = new File(dir, "presets.json");
        try {
            BufferedWriter bufferedWriter = Files.newWriter(file, StandardCharsets.UTF_8);
            JsonObject jsonObject = new JsonObject();

            for(StylePreset preset : BounceStylesRegistries.PRESETS.values()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("name", preset.name());
                obj.addProperty("head", preset.head() != null ? preset.head().toString() : "");
                obj.addProperty("body", preset.body() != null ? preset.body().toString() : "");
                obj.addProperty("legs", preset.legs() != null ? preset.legs().toString() : "");
                obj.addProperty("feet", preset.feet() != null ? preset.feet().toString() : "");
                jsonObject.add(preset.presetId().getPath(), obj);
            }

            GSON.toJson(jsonObject, bufferedWriter);
            bufferedWriter.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File getStylesDirectory() {
        return Platform.getGameFolder().resolve("styles").toFile();
    }
}
