package dev.bsmp.bouncestyles.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.renderer.StyleLayerRenderer;
import dev.bsmp.bouncestyles.networking.serverbound.OpenStyleScreenServerbound;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;

public class BounceStylesClient {
    public static final KeyMapping KEY_WARDROBE = new KeyMapping("key.bounce_styles.wardrobe", GLFW.GLFW_KEY_C, "key.bounce_styles.category");
    public static StyleLayerRenderer STYLE_RENDERER;

    public static void init() {
        KeyMappingRegistry.register(KEY_WARDROBE);
        ClientTickEvent.CLIENT_POST.register(instance -> { while (KEY_WARDROBE.consumeClick()) new OpenStyleScreenServerbound().sendToServer(); });
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, StyleLoader::loadStylePacks);
    }

    public static boolean shouldShowWarningForServer(int hashCode) {
        ServerData serverInfo = Minecraft.getInstance().getCurrentServer();
        if(serverInfo != null) {
            File rememberServerFile = Platform.getConfigFolder().resolve("style_ignore_servers.json").toFile();
            if(!rememberServerFile.exists()) return true;

            try(FileReader reader = new FileReader(rememberServerFile)) {
                Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                JsonObject json = gson.fromJson(reader, JsonObject.class);

                if(!json.has(serverInfo.ip)) return true;
                return json.get(serverInfo.ip).getAsInt() != hashCode;
            }
            catch (IOException e) {
                BounceStyles.LOGGER.error("Exception reading server memory file.");
                BounceStyles.LOGGER.error(e);
                return true;
            }
        }
        return false;
    }

    public static void addServerToIgnoreFile(int hashCode) {
        ServerData serverInfo = Minecraft.getInstance().getCurrentServer();
        if(serverInfo != null) {
            try {
                File rememberServerFile = Platform.getConfigFolder().resolve("style_ignore_servers.json").toFile();
                Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                JsonObject jsonObject;

                if (rememberServerFile.exists()) {
                    FileReader reader = new FileReader(rememberServerFile);
                    jsonObject = gson.fromJson(reader, JsonObject.class);
                    reader.close();
                }
                else {
                    jsonObject = new JsonObject();
                    rememberServerFile.createNewFile();
                }

                jsonObject.addProperty(serverInfo.ip, hashCode);

                FileWriter writer = new FileWriter(rememberServerFile);
                writer.write(gson.toJson(jsonObject));
                writer.close();
            }
            catch (IOException e) {
                BounceStyles.LOGGER.error("Exception modifying server memory file.");
                BounceStyles.LOGGER.error(e);
            }
        }
    }

    public static File writeMissingStyleLog(List<ResourceLocation> missingIds) {
        Path dir = Platform.getGameFolder().resolve("styles").resolve("logs");
        dir.toFile().mkdirs();
        File file = dir.resolve("missing_styles_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".log").toFile();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            StringJoiner joiner = new StringJoiner("\n");
            for(ResourceLocation id : missingIds) joiner.add(id.toString());
            writer.write(joiner.toString());
        }
        catch (IOException e) {
            BounceStyles.LOGGER.error("Exception occurred while writing Missing Style ID File " + file.getName());
            BounceStyles.LOGGER.error(e);
        }

        return file;
    }

    public static boolean isLookingForLang(ResourceLocation id) {
        String langCode = Minecraft.getInstance().getLanguageManager().getSelected();
        return id.getPath().endsWith(String.format("lang/%s.json", langCode));
    }

    public static IoSupplier<InputStream> processPackLangs(List<PackResources> packs, ResourceLocation id) {
        PackType type = PackType.CLIENT_RESOURCES;
        Gson gson = new Gson();

        JsonObject translationMap = null;
        for (PackResources pack : packs) {
            IoSupplier<InputStream> supplier = pack.getResource(type, id);
            if (supplier != null) {
                try (InputStream inputStream = supplier.get()) {
                    JsonObject obj = gson.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
                    if (translationMap == null)
                        translationMap = obj;
                    else {
                        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                            translationMap.add(entry.getKey(), entry.getValue());
                        }
                    }
                }
                catch (IOException e) {
                    BounceStyles.LOGGER.info("Exception processing Lang file for Style Pack: " + pack.packId());
                    BounceStyles.LOGGER.info(e);
                }
            }
        }
        if (translationMap != null) {
            InputStream stream = new ByteArrayInputStream(gson.toJson(translationMap).getBytes());
            return () -> stream;
        }
        return null;
    }
}
