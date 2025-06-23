//? if forge {
/*package dev.bsmp.bouncestyles.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.BounceStylesRegistries;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod(BounceStyles.modId)
@Mod.EventBusSubscriber(modid = BounceStyles.modId, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BounceStylesForge {

    public BounceStylesForge() {
        EventBuses.registerModEventBus(BounceStyles.modId, FMLJavaModLoadingContext.get().getModEventBus());
        BounceStyles.init();
    }

    @SubscribeEvent
    public static void registerDynamicRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(BounceStylesRegistries.STYLE_REGISTRY_KEY, Style.CODEC);
    }

}
*///?}