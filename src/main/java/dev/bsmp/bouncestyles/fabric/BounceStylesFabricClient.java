//? if fabric {
package dev.bsmp.bouncestyles.fabric;

import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BounceStylesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BounceStylesClient.init();
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(this::registerLayer);
    }

    private void registerLayer(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?,?> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
        if(entityType == EntityType.PLAYER)
            registrationHelper.register(BounceStylesClient.STYLE_RENDERER = new StyleLayerRenderer((RenderLayerParent<Player, PlayerModel<Player>>) livingEntityRenderer));
    }
}
//?}