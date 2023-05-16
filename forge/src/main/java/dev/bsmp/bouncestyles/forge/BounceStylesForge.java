package dev.bsmp.bouncestyles.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.pack.StylePackProvider;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BounceStyles.modId)
@Mod.EventBusSubscriber(modid = BounceStyles.modId, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BounceStylesForge {

    public BounceStylesForge() {
        EventBuses.registerModEventBus(BounceStyles.modId, FMLJavaModLoadingContext.get().getModEventBus());
        BounceStyles.init();
    }

    @SubscribeEvent
    public static void addPackProvider(AddPackFindersEvent event) {
        event.addRepositorySource(StylePackProvider.INSTANCE);
    }

}
