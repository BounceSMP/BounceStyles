package dev.bsmp.bouncestyles.client;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.bsmp.bouncestyles.client.renderer.StyleLayerRenderer;
import dev.bsmp.bouncestyles.networking.BounceStylesNetwork;
import dev.bsmp.bouncestyles.networking.packets.OpenStyleScreenServerbound;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class BounceStylesClient {
    public static final KeyBinding KEY_WARDROBE = new KeyBinding("key.bounce_styles.wardrobe", GLFW.GLFW_KEY_C, "key.bounce_styles.category");
    public static StyleLayerRenderer STYLE_RENDERER;

    public static void init() {
        BounceStylesNetwork.initClientbound();
        KeyMappingRegistry.register(KEY_WARDROBE);
        ClientTickEvent.CLIENT_POST.register(instance -> { while (KEY_WARDROBE.wasPressed()) new OpenStyleScreenServerbound().sendToServer(); });
    }

}
