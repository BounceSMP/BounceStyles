//? if neoforge {
/*package dev.bsmp.bouncestyles.neoforge;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

//? if >= 1.21.11 {
/^import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import dev.bsmp.bouncestyles.core.client.renderer.StyleGuiRenderer;
import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
^///? } else {
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.world.entity.player.Player;
import dev.bsmp.bouncestyles.core.client.renderer.LegacyStyleLayerRenderer;
//? }

@Mod(value = BounceStyles.modId, dist = Dist.CLIENT)
//? if >= 1.21.11
//@EventBusSubscriber(value = Dist.CLIENT)
public class BounceStylesNeoforgeClient {

    public BounceStylesNeoforgeClient(IEventBus bus) {
        BounceStylesClient.init();
        bus.addListener(BounceStylesNeoforgeClient::registerLayers);
    }

    static void registerLayers(EntityRenderersEvent.AddLayers event) {
        event.getSkins().forEach(model -> {
            //? if >= 1.21.11 {
            /^var playerRenderer = event.getPlayerRenderer(model);
            playerRenderer.addLayer(BounceStylesClient.getOrCreateStyleRenderer(playerRenderer));
            ^///? } else {
            var playerRenderer = (LivingEntityRenderer) event.getSkin(model);
            playerRenderer.addLayer(BounceStylesClient.getOrCreateStyleRenderer(playerRenderer));
            //? }
        });
    }

    //? if >= 1.21.11 {
    /^@SubscribeEvent
    static void registerPiPRenderer(RegisterPictureInPictureRenderersEvent event) {
        event.register(StyleGuiRenderer.StyleGuiRenderState.class, StyleGuiRenderer::new);
    }
    ^///? }

}
*///?}