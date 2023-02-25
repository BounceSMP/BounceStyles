package dev.bsmp.bouncestyles.client;

import dev.bsmp.bouncestyles.client.renderer.GarmentLayerRenderer;
import dev.bsmp.bouncestyles.client.screen.WardrobeScreen;
import dev.bsmp.bouncestyles.networking.SyncStyleDataS2C;
import dev.bsmp.bouncestyles.networking.SyncStyleUnlocksBi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class BounceStylesClient implements ClientModInitializer {
    public static final KeyMapping KEY_WARDROBE = new KeyMapping("key.bouncestyles.wardrobe", GLFW.GLFW_KEY_C, "key.bouncestyles.category");

    public static GarmentLayerRenderer GARMENT_RENDERER;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(KEY_WARDROBE);
        ClientTickEvents.END_CLIENT_TICK.register(client -> { while (KEY_WARDROBE.consumeClick()) SyncStyleUnlocksBi.sendToServer(); });
        ClientPlayNetworking.registerGlobalReceiver(SyncStyleDataS2C.ID, SyncStyleDataS2C::handle);
        ClientPlayNetworking.registerGlobalReceiver(SyncStyleUnlocksBi.ID_S2C, SyncStyleUnlocksBi::handleClient);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(this::registerRendering);
    }

    private void registerRendering(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?,?> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
        if(livingEntityRenderer instanceof PlayerRenderer) {
            registrationHelper.register(GARMENT_RENDERER = new GarmentLayerRenderer((RenderLayerParent<Player, PlayerModel<Player>>) livingEntityRenderer));
        }
    }

}
