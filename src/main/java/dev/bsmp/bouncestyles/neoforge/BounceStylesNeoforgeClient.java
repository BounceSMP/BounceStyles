//? if neoforge {
/*package dev.bsmp.bouncestyles.neoforge;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import dev.bsmp.bouncestyles.core.client.renderer.StyleLayerRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = BounceStyles.modId, dist = Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class BounceStylesNeoforgeClient {

    public BounceStylesNeoforgeClient(IEventBus bus) {
        BounceStylesClient.init();
    }

    @SubscribeEvent
    static void registerLayers(EntityRenderersEvent.AddLayers event) {
        event.getSkins().forEach(model -> {
            LivingEntityRenderer<Player, PlayerModel<Player>> playerRenderer = event.getSkin(model);
            playerRenderer.addLayer(BounceStylesClient.STYLE_RENDERER = new StyleLayerRenderer(playerRenderer));
        });
    }

}
*///?}