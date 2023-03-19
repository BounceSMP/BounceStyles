package dev.bsmp.bouncestyles.client;

import dev.bsmp.bouncestyles.client.renderer.StyleLayerRenderer;
import dev.bsmp.bouncestyles.networking.SyncStyleDataS2C;
import dev.bsmp.bouncestyles.networking.SyncStyleUnlocksBi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class BounceStylesClient implements ClientModInitializer {
    public static final KeyBinding KEY_WARDROBE = new KeyBinding("key.bounce_styles.wardrobe", GLFW.GLFW_KEY_C, "key.bounce_styles.category");

    public static StyleLayerRenderer STYLE_RENDERER;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(KEY_WARDROBE);
        ClientTickEvents.END_CLIENT_TICK.register(client -> { while (KEY_WARDROBE.wasPressed()) SyncStyleUnlocksBi.sendToServer(); });
        ClientPlayNetworking.registerGlobalReceiver(SyncStyleDataS2C.ID, SyncStyleDataS2C::handle);
        ClientPlayNetworking.registerGlobalReceiver(SyncStyleUnlocksBi.ID_S2C, SyncStyleUnlocksBi::handleClient);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(this::registerRendering);
    }

    private void registerRendering(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?,?> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererFactory.Context context) {
        if(livingEntityRenderer instanceof PlayerEntityRenderer) {
            registrationHelper.register(STYLE_RENDERER = new StyleLayerRenderer((FeatureRendererContext<PlayerEntity, PlayerEntityModel<PlayerEntity>>) livingEntityRenderer));
        }
    }

}
