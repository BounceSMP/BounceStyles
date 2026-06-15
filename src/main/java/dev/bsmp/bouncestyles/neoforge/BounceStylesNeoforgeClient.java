//? if neoforge {
/*package dev.bsmp.bouncestyles.neoforge;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import dev.bsmp.bouncestyles.core.client.renderer.StyleGuiRenderer;import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;

@Mod(value = BounceStyles.modId, dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class BounceStylesNeoforgeClient {

    public BounceStylesNeoforgeClient(IEventBus bus) {
        BounceStylesClient.init();
    }

    @SubscribeEvent
    static void registerLayers(EntityRenderersEvent.AddLayers event) {
        event.getSkins().forEach(model -> {
            //? if >= 1.21.11 {
            var playerRenderer = event.getPlayerRenderer(model);
            //? } else {
//            var playerRenderer = event.getSkin(model);
            //? }
            playerRenderer.addLayer(BounceStylesClient.STYLE_RENDERER = new StyleLayerRenderer(playerRenderer));
        });
    }

    @SubscribeEvent
    static void registerPiPRenderer(RegisterPictureInPictureRenderersEvent event) {
        event.register(StyleGuiRenderer.StyleGuiRenderState.class, StyleGuiRenderer::new);
    }

}
*///?}