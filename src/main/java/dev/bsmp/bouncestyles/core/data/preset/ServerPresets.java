package dev.bsmp.bouncestyles.core.data.preset;

import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.bsmp.bouncestyles.api.style.StylePreset;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.networking.clientbound.SyncPresetsClientbound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ServerPresets {
    public static final LevelResource LEVEL_DIR_PRESETS = new LevelResource("style_presets");

    public static Optional<Map<String, StylePreset>> getGlobalPresets(MinecraftServer server) {
        var file = getFile(server, "_global.json");

        if (!file.exists()) {
            writePresetsFile(file, Map.of());
            return Optional.empty();
        }

        return loadPresetsFromFile(file);
    }

    public static Optional<Map<String, StylePreset>> getPlayerPresets(ServerPlayer player) {
        return loadPresetsFromFile(getFile(player.level().getServer(), player.getUUID()+".json"));
    }

    public static Map<String, StylePreset> getMergedPresets(ServerPlayer player) {
        var map = new HashMap<String, StylePreset>();

        map.putAll(getGlobalPresets(player.level().getServer()).orElse(Map.of()));
        map.putAll(getPlayerPresets(player).orElse(Map.of()));

        return map;
    }

    private static File getFile(MinecraftServer server, String fileName) {
        var directory = server.getWorldPath(LEVEL_DIR_PRESETS).toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return new File(directory, fileName);
    }

    public static void createPlayerPreset(ServerPlayer player, String presetName, StylePreset preset) {
        var file = getFile(player.level().getServer(), player.getUUID()+".json");
        var playerPresets = new HashMap<>(loadPresetsFromFile(file).orElse(Map.of()));

        if (preset != null)
            playerPresets.put(presetName, preset);
        else
            playerPresets.remove(presetName);

        writePresetsFile(file, playerPresets);
    }

    private static Optional<Map<String, StylePreset>> loadPresetsFromFile(File file) {
        if (!file.exists())
            return Optional.empty();

        try (BufferedReader reader = Files.newReader(file, StandardCharsets.UTF_8)) {
            JsonObject jsonObject = BounceStyles.GSON.fromJson(reader, JsonObject.class);
            return MAP_CODEC.parse(JsonOps.INSTANCE, jsonObject).resultOrPartial();
        }
        catch (Exception e) {
            BounceStyles.LOGGER.error("Exception Occurred reading preset file '{}'", file.getName(), e);
        }

        return Optional.empty();
    }

    public static void writePresetsFile(File file, Map<String, StylePreset> map) {
        try {
            if (!file.exists()) file.createNewFile();

            try (BufferedWriter bufferedWriter = Files.newWriter(file, StandardCharsets.UTF_8)) {
                MAP_CODEC.encodeStart(JsonOps.INSTANCE, map).resultOrPartial()
                        .ifPresent(jsonElement -> BounceStyles.GSON.toJson(jsonElement, bufferedWriter));
            }
        }
        catch (Exception e) {
            BounceStyles.LOGGER.error("Exception Occurred writing preset file '{}'", file.getName(), e);
        }
    }

    public static void syncToPlayer(ServerPlayer player) {
        new SyncPresetsClientbound(getGlobalPresets(player.level().getServer()).orElse(Map.of()), getPlayerPresets(player).orElse(Map.of())).sendToPlayer(player);
    }

    public static final Codec<Map<String, StylePreset>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, StylePreset.CODEC);
}
