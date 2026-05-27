//? if fabric {
package dev.bsmp.bouncestyles.fabric;

import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
//? if >= 1.21.11 {
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.model.player.PlayerModel;
//? } else {
//import net.minecraft.client.model.PlayerModel;
//? }

public class BounceStylesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BounceStylesClient.init();
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(this::registerLayer);
    }

    //? if >= 1.21.11 {
    private void registerLayer(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?,?, ?> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
    //? } else
    //private void registerLayer(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?,?> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
        if(entityType == EntityType.PLAYER || entityType == EntityType.MANNEQUIN)
            //? if >= 1.21.11 {
            registrationHelper.register(BounceStylesClient.STYLE_RENDERER = new StyleLayerRenderer((RenderLayerParent<AvatarRenderState, PlayerModel>) livingEntityRenderer));
            //? } else {
//            registrationHelper.register(BounceStylesClient.STYLE_RENDERER = new StyleLayerRenderer((RenderLayerParent<Player, PlayerModel<Player>>) livingEntityRenderer));
            //? }
    }
}
//?}