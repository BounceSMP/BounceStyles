package dev.bsmp.bouncestyles.core.client;

import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.data.preset.ClientPresets;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.lwjgl.glfw.GLFW;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Keybinds {
    private static final KeyMapping KEY_WARDROBE = createWardrobeKeyMapping();
    private static final Map<String, InputConstants.Key> PRESET_TO_KEY = new HashMap<>();
    private static final Map<InputConstants.Key, String> KEY_TO_PRESET = new HashMap<>();

    public static void register() {
        KeyMappingRegistry.register(KEY_WARDROBE);
    }

    public static void handleKeys(InputConstants.Key key) {
        while (KEY_WARDROBE.consumeClick()) new OpenStyleScreenServerbound().sendToServer();

        ClientPresets.getPreset(getPresetForKey(key)).ifPresent(preset ->
                new EquipStyleServerbound(preset.toMap()).sendToServer()
        );
    }

    private static KeyMapping createWardrobeKeyMapping() {
        //? if >= 1.21.10 {
        return new KeyMapping("key.bounce_styles.wardrobe", GLFW.GLFW_KEY_C, KeyMapping.Category.register(BounceStyles.id("key.bounce_styles.category")));
        //? } else {
        /*return new KeyMapping("key.bounce_styles.wardrobe", GLFW.GLFW_KEY_C, "key.bounce_styles.category");
         *///? }
    }

    public static void addPresetKeybind(String presetName, InputConstants.Key key) {
        removePreset(presetName);
        removeKey(key);
        if (key != null) addKeybind(presetName, key);
        savePresetKeybinds();
    }

    private static void addKeybind(String presetName, InputConstants.Key key) {
        PRESET_TO_KEY.put(presetName, key);
        KEY_TO_PRESET.put(key, presetName);
    }

    private static void removeKey(InputConstants.Key key) {
        var presetName = KEY_TO_PRESET.remove(key);
        if (presetName != null) PRESET_TO_KEY.remove(presetName);
    }

    private static void removePreset(String presetName) {
        var key = PRESET_TO_KEY.remove(presetName);
        if (key != null) KEY_TO_PRESET.remove(key);
    }

    public static InputConstants.Key getKeyForPreset(String presetName) {
        return PRESET_TO_KEY.get(presetName);
    }

    public static String getPresetForKey(InputConstants.Key key) {
        return KEY_TO_PRESET.get(key);
    }

    private static String getWorldName() {
        if (Minecraft.getInstance().getSingleplayerServer() != null)
            return  Minecraft.getInstance().getSingleplayerServer().getWorldData().getLevelName();
        else
            return Minecraft.getInstance().getConnection().getServerData().ip;
    }

    private static File file() {
        return Platform.getConfigFolder().resolve("bounce_styles_preset_hotkeys.json").toFile();
    }

    public static void loadPresetKeybinds() {
        PRESET_TO_KEY.clear();
        KEY_TO_PRESET.clear();

        BounceStyles.LOGGER.info("Loading preset keybinds...");
        loadPresetsFile().ifPresent(map ->
            map.getOrDefault(getWorldName(), new HashMap<>()).forEach(Keybinds::addKeybind)
        );
        BounceStyles.LOGGER.info(PRESET_TO_KEY);
    }

    private static Optional<Map<String, Map<String, InputConstants.Key>>> loadPresetsFile() {
        var file = file();

        if (!file.exists())
            return Optional.empty();

        try (BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8)) {
            JsonObject jsonObject = BounceStyles.GSON.fromJson(reader, JsonObject.class);
            return MAP_CODEC.parse(JsonOps.INSTANCE, jsonObject).resultOrPartial();
        }
        catch (Exception e) {
            BounceStyles.LOGGER.error("Exception Occurred reading preset hotkeys file", file.getName(), e);
        }

        return Optional.empty();
    }

    public static void savePresetKeybinds() {
        var keybindConfig = loadPresetsFile().map(HashMap::new).orElse(new HashMap<>());
        keybindConfig.put(getWorldName(), PRESET_TO_KEY);
        writePresetsFile(keybindConfig);
    }

    private static void writePresetsFile(Map<String, Map<String, InputConstants.Key>> map) {
        try {
            var file = file();
            if (!file.exists()) file.createNewFile();

            try (BufferedWriter bufferedWriter = Files.newWriter(file, StandardCharsets.UTF_8)) {
                MAP_CODEC.encodeStart(JsonOps.INSTANCE, map).resultOrPartial()
                        .ifPresent(jsonElement -> BounceStyles.GSON.toJson(jsonElement, bufferedWriter));
            }
        }
        catch (Exception e) {
            BounceStyles.LOGGER.error("Exception Occurred writing preset keybinds file", e);
        }
    }

    private static DataResult<InputConstants.Key> decodeKey(String keyName) {
        try {
            return DataResult.success(InputConstants.getKey(keyName));
        }
        catch (IllegalArgumentException e) {
            return DataResult.error(() -> "Not a valid key name: '" + keyName + "' " + e.getMessage());
        }
    }

    private static final Codec<Map<String, Map<String, InputConstants.Key>>> MAP_CODEC = Codec.unboundedMap(
            Codec.STRING,
            Codec.unboundedMap(
                    Codec.STRING,
                    Codec.STRING.comapFlatMap(Keybinds::decodeKey, InputConstants.Key::getName)
            )
    );
}
