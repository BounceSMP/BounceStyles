package dev.bsmp.bouncestyles.core.data.preset;

import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.api.style.StylePreset;
import dev.bsmp.bouncestyles.core.networking.serverbound.UpdatePresetServerbound;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClientPresets {
    private static final Map<String, StylePreset> GLOBAL_PRESETS = new HashMap<>();
    private static final Map<String, StylePreset> PLAYER_PRESETS = new HashMap<>();

    public static Map<String, StylePreset> getGlobalPresets() {
        return new HashMap<>(GLOBAL_PRESETS);
    }

    public static Map<String, StylePreset> getPlayerPresets() {
        return new HashMap<>(PLAYER_PRESETS);
    }

    public static void loadPresets(Map<String, StylePreset> global, Map<String, StylePreset> player) {
        GLOBAL_PRESETS.clear();
        GLOBAL_PRESETS.putAll(global);

        PLAYER_PRESETS.clear();
        PLAYER_PRESETS.putAll(player);
    }

    public static Optional<StylePreset> getPreset(String presetName) {
        if (presetName == null) return Optional.empty();
        var preset = GLOBAL_PRESETS.get(presetName);
        if (preset == null) preset = PLAYER_PRESETS.get(presetName);
        return Optional.ofNullable(preset);
    }

    public static boolean createPreset(StyleData data, String presetName) {
        var preset = new StylePreset(data.getHeadStyle(), data.getBodyStyle(), data.getLegsStyle(), data.getFeetStyle());
        var replaced = PLAYER_PRESETS.put(presetName, preset) != null;
        new UpdatePresetServerbound(presetName, preset).sendToServer();
        return replaced;
    }

    public static boolean deletePreset(String presetName) {
        var success = PLAYER_PRESETS.remove(presetName) != null;
        new UpdatePresetServerbound(presetName).sendToServer();
        return success;
    }
}
