package dev.bsmp.bouncestyles.forge.client;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.client.BounceStylesClient;
import dev.bsmp.bouncestyles.client.renderer.StyleLayerRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BounceStyles.modId, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BounceStylesForgeClient {

    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        BounceStylesClient.init();
    }

    @SubscribeEvent
    static void registerLayer(EntityRenderersEvent.AddLayers event) {
        for (String s : event.getSkins()) {
            LivingEntityRenderer<Player, PlayerModel<Player>> playerRenderer = event.getSkin(s);
            playerRenderer.addLayer(BounceStylesClient.STYLE_RENDERER = new StyleLayerRenderer(playerRenderer));
        }
    }
}
