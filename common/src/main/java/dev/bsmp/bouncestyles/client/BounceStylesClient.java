package dev.bsmp.bouncestyles.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.renderer.StyleLayerRenderer;
import dev.bsmp.bouncestyles.networking.serverbound.OpenStyleScreenServerbound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

public class BounceStylesClient {
    public static final KeyBinding KEY_WARDROBE = new KeyBinding("key.bounce_styles.wardrobe", GLFW.GLFW_KEY_C, "key.bounce_styles.category");
    public static StyleLayerRenderer STYLE_RENDERER;

    public static void init() {
        KeyMappingRegistry.register(KEY_WARDROBE);
        ClientTickEvent.CLIENT_POST.register(instance -> { while (KEY_WARDROBE.wasPressed()) new OpenStyleScreenServerbound().sendToServer(); });
        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, StyleLoader::loadStylePacks);
    }

    public static boolean shouldShowWarningForServer(int hashCode) {
        ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
        if(serverInfo != null) {
            File rememberServerFile = Platform.getConfigFolder().resolve("style_ignore_servers.json").toFile();
            if(!rememberServerFile.exists()) return true;

            try(FileReader reader = new FileReader(rememberServerFile)) {
                Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
                JsonObject json = gson.fromJson(reader, JsonObject.class);

                if(!json.has(serverInfo.address)) return true;
                return json.get(serverInfo.address).getAsInt() != hashCode;
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
        ServerInfo serverInfo = MinecraftClient.getInstance().getCurrentServerEntry();
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

                jsonObject.addProperty(serverInfo.address, hashCode);

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

    public static File writeMissingStyleLog(List<Identifier> missingIds) {
        Path dir = Platform.getGameFolder().resolve("styles").resolve("logs");
        dir.toFile().mkdirs();
        File file = dir.resolve("missing_styles_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".log").toFile();

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            StringJoiner joiner = new StringJoiner("\n");
            for(Identifier id : missingIds) joiner.add(id.toString());
            writer.write(joiner.toString());
        }
        catch (IOException e) {
            BounceStyles.LOGGER.error("Exception occurred while writing Missing Style ID File " + file.getName());
            BounceStyles.LOGGER.error(e);
        }

        return file;
    }
}
