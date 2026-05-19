//? if neoforge {
package dev.bsmp.bouncestyles.neoforge;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

//? if >= 1.21.11 {
import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
//? } else {
//import net.minecraft.client.model.PlayerModel;
//import dev.bsmp.bouncestyles.core.client.renderer.LegacyStyleLayerRenderer;
//import net.minecraft.client.renderer.entity.LivingEntityRenderer;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//? }

@Mod(value = BounceStyles.modId, dist = Dist.CLIENT)
//? if >= 1.21.11 {
@EventBusSubscriber(value = Dist.CLIENT)
//? } else
//@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class BounceStylesNeoforgeClient {

    public BounceStylesNeoforgeClient(IEventBus bus) {
        BounceStylesClient.init();
    }

    @SubscribeEvent
    static void registerLayers(EntityRenderersEvent.AddLayers event) {
        //? if >= 1.21.11 {
        event.getSkins().forEach(model -> {
            var playerRenderer = event.getPlayerRenderer(model);
            playerRenderer.addLayer(BounceStylesClient.STYLE_RENDERER = new StyleLayerRenderer(playerRenderer));
        });
        //? } else {
//        event.getSkins().forEach(model -> {
//            LivingEntityRenderer<Player, PlayerModel<Player>> playerRenderer = event.getSkin(model);
//            playerRenderer.addLayer(BounceStylesClient.STYLE_RENDERER = new LegacyStyleLayerRenderer(playerRenderer));
//        });
        //? }
    }

}
//?}