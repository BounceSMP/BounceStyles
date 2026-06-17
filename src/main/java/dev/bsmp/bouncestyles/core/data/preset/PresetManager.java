package dev.bsmp.bouncestyles.core.data.preset;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.bsmp.bouncestyles.api.style.StylePreset;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.core.data.style.StyleLoader;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PresetManager {
    public static void loadPresets() {
        File dir = StyleLoader.getStylesDirectory();
        File file = new File(dir, "presets.json");
        if(file.exists()) {
            boolean convert = false;
            try(BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8)) {
                try {
                    JsonObject jsonObject = StyleLoader.GSON.fromJson(reader, JsonObject.class);
                    Codec.unboundedMap(Codec.STRING, StylePreset.CODEC).parse(JsonOps.INSTANCE, jsonObject)
                            .resultOrPartial(BounceStyles.LOGGER::error)
                            .ifPresent(BounceStylesClient::setPresets);
                }
                catch (JsonSyntaxException e) {
                    JsonArray jsonArray = StyleLoader.GSON.fromJson(reader, JsonArray.class);
                    StylePreset.CODEC.listOf().parse(JsonOps.INSTANCE, jsonArray)
                            .resultOrPartial(BounceStyles.LOGGER::error)
                            .ifPresent(presets -> {
                                int i = 0;
                                presets.forEach(stylePreset -> BounceStylesClient.getPresets().put("preset_" + i, stylePreset));
                            });
                    convert = true;
                }
            }
            catch (IOException e) {
                BounceStyles.LOGGER.error("Exception Occurred reading Presets file", e);
            }
            if (convert)
                writePresetsFile();
        }
    }

    public static void removePreset(String presetName) {
        BounceStylesClient.getPresets().remove(presetName);
        writePresetsFile();
    }

    public static void writePresetsFile() {
        File dir = StyleLoader.getStylesDirectory();
        File file = new File(dir, "presets.json");
        try(BufferedWriter bufferedWriter = Files.newWriter(file, StandardCharsets.UTF_8)) {
            Codec.unboundedMap(Codec.STRING, StylePreset.CODEC)
                    .encodeStart(JsonOps.INSTANCE, BounceStylesClient.getPresets())
                    .resultOrPartial(BounceStyles.LOGGER::error)
                    .ifPresent(jsonElement -> StyleLoader.GSON.toJson(jsonElement, bufferedWriter));
        }
        catch (IOException e) {
            BounceStyles.LOGGER.error("Exception Occurred writing Presets file", e);
        }
    }

    public static StylePreset createPreset(StyleData styleData, String presetName) {
        StylePreset newPreset = styleData.createPreset();
        BounceStylesClient.getPresets().put(presetName, newPreset);
        writePresetsFile();
        return newPreset;
    }
}
