package dev.bsmp.bouncestyles.core.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.lwjgl.glfw.GLFW;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class BounceStylesClient {
    public static final KeyMapping KEY_WARDROBE = new KeyMapping("key.bounce_styles.wardrobe", GLFW.GLFW_KEY_C, "key.bounce_styles.category");
    public static StyleLayerRenderer STYLE_RENDERER;

    public static void init() {
        KeyMappingRegistry.register(KEY_WARDROBE);
        ClientTickEvent.CLIENT_POST.register(instance -> { while (KEY_WARDROBE.consumeClick()) new OpenStyleScreenServerbound().sendToServer(); });
//        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, StyleLoader::loadStylePacks);
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
                    BounceStyles.LOGGER.info("Exception processing Lang file for Style Pack: {}", pack.packId());
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