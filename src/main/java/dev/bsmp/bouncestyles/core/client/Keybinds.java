package dev.bsmp.bouncestyles.core.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.data.preset.ClientPresets;
import dev.bsmp.bouncestyles.core.networking.serverbound.EquipStyleServerbound;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class Keybinds {
    private static final KeyMapping KEY_WARDROBE = createWardrobeKeyMapping();
    private static final Map<String, InputConstants.Key> PRESET_TO_KEY = new HashMap<>();
    private static final Map<InputConstants.Key, String> KEY_TO_PRESET = new HashMap<>();

    //ToDo Hotkey per-world/server serialization

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
}
