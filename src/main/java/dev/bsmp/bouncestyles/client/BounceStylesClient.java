package dev.bsmp.bouncestyles.client;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.client.renderer.StyleLayerRenderer;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.data.StyleData;
import dev.bsmp.bouncestyles.networking.BounceStylesNetwork;
import dev.bsmp.bouncestyles.networking.SyncStyleUnlocksBi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = BounceStyles.modId, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BounceStylesClient {
    public static final KeyBinding KEY_WARDROBE = new KeyBinding("key.bouncestyles.wardrobe", GLFW.GLFW_KEY_C, "key.bouncestyles.category");
    public static StyleLayerRenderer STYLE_RENDERER;

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(KEY_WARDROBE);
    }

    @SubscribeEvent
    static void registerRendering(EntityRenderersEvent.AddLayers event) {
        for (String s : event.getSkins()) {
            LivingEntityRenderer<PlayerEntity, PlayerEntityModel<PlayerEntity>> playerRenderer = event.getSkin(s);
            playerRenderer.addFeature(STYLE_RENDERER = new StyleLayerRenderer(playerRenderer));
        }
    }

    public static void syncStyle(int playerID, StyleData styleData) {
        Entity entity = MinecraftClient.getInstance().world.getEntityById(playerID);
        if(entity instanceof PlayerEntity)
            StyleData.setPlayerData((PlayerEntity) entity, styleData);
    }

    public static void openWardrobeScreen(StyleData styleData) {
        StyleData.setPlayerData(MinecraftClient.getInstance().player, styleData);
        MinecraftClient.getInstance().setScreen(new WardrobeScreen(styleData.getUnlocks()));
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = BounceStyles.modId, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandler {

        @SubscribeEvent
        static void keyEvent(InputEvent.KeyInputEvent event) {
            if(KEY_WARDROBE.isPressed())
                BounceStylesNetwork.NETWORK.sendToServer(new SyncStyleUnlocksBi());
        }

    }

}
