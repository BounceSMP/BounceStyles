package dev.bsmp.bouncestyles.core.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.api.style.StylePreset;
import dev.bsmp.bouncestyles.core.networking.serverbound.OpenStyleScreenServerbound;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.lwjgl.glfw.GLFW;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BounceStylesClient {
    private static final HashMap<String, StylePreset> PRESETS = new HashMap<>(); //ToDo add server->client syncing; adds serverside presets in addition to existing client presets

    public static final KeyMapping KEY_WARDROBE = createWardrobeKeyMapping();
    //? if >= 1.21.5 {
    public static dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer STYLE_RENDERER;
    //? } else
    //public static dev.bsmp.bouncestyles.core.client.renderer.LegacyStyleLayerRenderer STYLE_RENDERER;

    public static void init() {
        KeyMappingRegistry.register(KEY_WARDROBE);
        ClientTickEvent.CLIENT_POST.register(instance -> { while (KEY_WARDROBE.consumeClick()) new OpenStyleScreenServerbound().sendToServer(); });
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(world -> BounceStylesRegistries.setRegistryAccess(world.registryAccess()));
    }

    public static void setPresets(Map<String, StylePreset> map) {
        PRESETS.clear();
        PRESETS.putAll(map);
    }

    public static Map<String, StylePreset> getPresets() {
        return PRESETS;
    }

    public static boolean isLookingForLang(Identifier id) {
        String langCode = Minecraft.getInstance().getLanguageManager().getSelected();
        return id.getPath().endsWith(String.format("lang/%s.json", langCode));
    }

    public static void onStyleUpdate() {
        if (Minecraft.getInstance().screen instanceof WardrobeScreen screen) {
            screen.refresh();
        }
    }

    private static KeyMapping createWardrobeKeyMapping() {
        //? if >= 1.21.10 {
        return new KeyMapping("key.bounce_styles.wardrobe", GLFW.GLFW_KEY_C, KeyMapping.Category.register(BounceStyles.id("key.bounce_styles.category")));
        //? } else {
        /*return new KeyMapping("key.bounce_styles.wardrobe", GLFW.GLFW_KEY_C, "key.bounce_styles.category");
        *///? }
    }

    public static IoSupplier<InputStream> processPackLangs(List<PackResources> packs, Identifier id) {
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