package dev.bsmp.bouncestyles.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.forge.client.BounceStylesForgeClient;
import dev.bsmp.bouncestyles.networking.packets.SyncStyleDataClientbound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BounceStyles.modId)
public class BounceStylesForge {

    public BounceStylesForge() {
        EventBuses.registerModEventBus(BounceStyles.modId, FMLJavaModLoadingContext.get().getModEventBus());
        BounceStyles.init();
    }

}
