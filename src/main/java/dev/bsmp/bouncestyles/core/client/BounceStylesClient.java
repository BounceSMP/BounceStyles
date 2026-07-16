package dev.bsmp.bouncestyles.core.client;

//~ if >= 1.21.5 'LegacyStyleLayerRenderer' -> 'StyleLayerRenderer' {

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
import dev.bsmp.bouncestyles.core.client.screen.WardrobeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class BounceStylesClient {
    private static StyleLayerRenderer STYLE_RENDERER;

    public static void init() {
        Keybinds.register();
        ClientLifecycleEvent.CLIENT_LEVEL_LOAD.register(world -> BounceStylesRegistries.setRegistryAccess(world.registryAccess()));
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> {
            if (player == Minecraft.getInstance().player)
                Keybinds.loadPresetKeybinds();
        });
    }

    public static StyleLayerRenderer getStyleRenderer() {
        return STYLE_RENDERER;
    }

    public static StyleLayerRenderer getOrCreateStyleRenderer(RenderLayerParent renderer) {
        if (STYLE_RENDERER == null) STYLE_RENDERER = new StyleLayerRenderer(renderer);
        return STYLE_RENDERER;
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
//~ }