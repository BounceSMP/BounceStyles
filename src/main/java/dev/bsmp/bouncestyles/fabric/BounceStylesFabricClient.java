//? if fabric {
package dev.bsmp.bouncestyles.fabric;

import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
//? if >= 1.21.5 {
import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
import dev.bsmp.bouncestyles.core.client.renderer.StyleGuiRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.model.player.PlayerModel;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
//? } else {
/*import net.minecraft.world.entity.player.Player;
import net.minecraft.client.model.player.PlayerModel;
import dev.bsmp.bouncestyles.core.client.renderer.LegacyStyleLayerRenderer;
*///? }

public class BounceStylesFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BounceStylesClient.init();
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(this::registerLayer);
        //? if >= 1.21.5
        SpecialGuiElementRegistry.register(ctx -> new StyleGuiRenderer(ctx.vertexConsumers()));
    }

    //? if >= 1.21.11 {
    private void registerLayer(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?,?, ?> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
    //? } else
    //private void registerLayer(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?,?> livingEntityRenderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {
        //? if >= 1.21.9 {
        if(entityType == EntityType.PLAYER || entityType == EntityType.MANNEQUIN)
        //? } else
        //if(entityType == EntityType.PLAYER)
            //? if >= 1.21.11 {
            registrationHelper.register(BounceStylesClient.STYLE_RENDERER = new StyleLayerRenderer((RenderLayerParent<AvatarRenderState, PlayerModel>) livingEntityRenderer));
            //? } else {
            /*registrationHelper.register(BounceStylesClient.STYLE_RENDERER = new LegacyStyleLayerRenderer((RenderLayerParent<Player, PlayerModel<Player>>) livingEntityRenderer));
            *///? }
    }
}
//?}