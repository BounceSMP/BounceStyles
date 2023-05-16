package dev.bsmp.bouncestyles.fabric.client;

import dev.bsmp.bouncestyles.client.BounceStylesClient;
import dev.bsmp.bouncestyles.client.renderer.StyleLayerRenderer;
import dev.bsmp.bouncestyles.mixin.ResourcePackManagerAccessor;
import dev.bsmp.bouncestyles.pack.StylePackProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class BounceStylesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BounceStylesClient.init();
        ((ResourcePackManagerAccessor) MinecraftClient.getInstance().getResourcePackManager()).getProviders().add(StylePackProvider.INSTANCE);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(this::registerLayer);
    }

    private void registerLayer(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?,?> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererFactory.Context context) {
        if(entityType == EntityType.PLAYER)
            registrationHelper.register(BounceStylesClient.STYLE_RENDERER = new StyleLayerRenderer((FeatureRendererContext<PlayerEntity, PlayerEntityModel<PlayerEntity>>) livingEntityRenderer));
    }
}
