package dev.bsmp.bouncestyles.forge.client;

import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.client.BounceStylesClient;
import dev.bsmp.bouncestyles.client.renderer.StyleLayerRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
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
            LivingEntityRenderer<PlayerEntity, PlayerEntityModel<PlayerEntity>> playerRenderer = event.getSkin(s);
            playerRenderer.addFeature(BounceStylesClient.STYLE_RENDERER = new StyleLayerRenderer(playerRenderer));
        }
    }

}
